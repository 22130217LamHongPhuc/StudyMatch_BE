FROM python:3.12-slim

WORKDIR /app

ENV PYTHONDONTWRITEBYTECODE=1
ENV PYTHONUNBUFFERED=1
ENV PORT=8080
ENV TZ=Asia/Ho_Chi_Minh

# Install dependencies
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Pre-download Hugging Face model and tokenizer to cache them in the image.
# This ensures that Cloud Run starting up is fast (cold-start mitigation).
RUN python -c "from transformers import AutoTokenizer, AutoModelForSeq2SeqLM; \
               AutoTokenizer.from_pretrained('tarudesu/ViHateT5-base-HSD', cache_dir='./models'); \
               AutoModelForSeq2SeqLM.from_pretrained('tarudesu/ViHateT5-base-HSD', cache_dir='./models')"

# Copy the application source code
COPY . .

# Expose the port
EXPOSE 8080

# Command to run the application using uvicorn
CMD ["sh", "-c", "uvicorn API:app --host 0.0.0.0 --port ${PORT}"]
