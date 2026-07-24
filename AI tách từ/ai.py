import threading
from typing import List, Union

import torch
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel, Field
from transformers import AutoTokenizer, AutoModelForSeq2SeqLM


MODEL_NAME = "tarudesu/ViHateT5-base-HSD"
MODEL_CACHE_DIR = "./models"

device = "cuda" if torch.cuda.is_available() else "cpu"

tokenizer = AutoTokenizer.from_pretrained(
    MODEL_NAME,
    cache_dir=MODEL_CACHE_DIR
)

model = AutoModelForSeq2SeqLM.from_pretrained(
    MODEL_NAME,
    cache_dir=MODEL_CACHE_DIR,
    torch_dtype=torch.float16 if device == "cuda" else torch.float32
)

model = model.to(device)
model.eval()

model_lock = threading.Lock()

app = FastAPI(
    title="Vietnamese Hate Speech Detection API",
    version="1.0.0"
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


class MessageRequest(BaseModel):
    id: Union[int, str]
    content: str = Field(..., min_length=1, max_length=1000)


class MessageModerateResult(BaseModel):
    id: Union[int, str]
    content: str
    label: str


def predict_batch(
    texts: list[str],
    prefix: str = "hate-speech-detection"
) -> list[str]:
    input_texts = [f"{prefix}: {text}" for text in texts]

    inputs = tokenizer(
        input_texts,
        return_tensors="pt",
        padding=True,
        truncation=True,
        max_length=256
    )

    inputs = {key: value.to(device) for key, value in inputs.items()}

    with model_lock:
        with torch.inference_mode():
            output_ids = model.generate(
                **inputs,
                max_new_tokens=8,
                num_beams=1,
                do_sample=False
            )

    return tokenizer.batch_decode(
        output_ids,
        skip_special_tokens=True
    )


@app.get("/health")
def health_check():
    return {
        "status": "ok",
        "device": device,
        "model": MODEL_NAME
    }


@app.post("/moderate/messages", response_model=list[MessageModerateResult])
def moderate_messages(messages: List[MessageRequest]):
    contents = [message.content for message in messages]

    labels = predict_batch(contents)

    results = []

    for message, label in zip(messages, labels):
        results.append(
            MessageModerateResult(
                id=message.id,
                content=message.content,
                label=label
            )
        )

    return results