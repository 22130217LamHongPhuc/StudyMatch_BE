import sys
import torch
from transformers import AutoTokenizer, AutoModelForSeq2SeqLM

MODEL_NAME = "tarudesu/ViHateT5-base-HSD"

device = "cuda" if torch.cuda.is_available() else "cpu"

tokenizer = AutoTokenizer.from_pretrained(
    MODEL_NAME,
    cache_dir="./models"
)

model = AutoModelForSeq2SeqLM.from_pretrained(
    MODEL_NAME,
    cache_dir="./models",
    torch_dtype=torch.float16 if device == "cuda" else torch.float32
)

model = model.to(device)
model.eval()


def predict_batch(texts: list[str], prefix: str = "hate-speech-detection") -> list[str]:
    input_texts = [f"{prefix}: {text}" for text in texts]

    inputs = tokenizer(
        input_texts,
        return_tensors="pt",
        padding=True,
        truncation=True,
        max_length=256
    )

    inputs = {k: v.to(device) for k, v in inputs.items()}

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


def predict(text: str) -> str:
    return predict_batch([text])[0]


def main():
    samples = [
        "Bạn học rất tốt",
        "Mày là đồ ngu",
        "con chó này"
    ]

    results = predict_batch(samples)

    for text, result in zip(samples, results):
        print(text, "=>", result)

    sys.exit(0)


if __name__ == "__main__":
    main()