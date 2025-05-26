import os
from fastapi.testclient import TestClient
from process_image import app

client = TestClient(app)

SAMPLE_IMAGE = os.path.join(os.path.dirname(__file__), "sample.jpg")


def test_extract_exif():
    with open(SAMPLE_IMAGE, "rb") as f:
        response = client.post("/extract_exif/", files={"file": ("sample.jpg", f, "image/jpeg")})
    assert response.status_code == 200
    data = response.json()
    assert "Timestamp" in data
    assert "Latitude" in data
    assert "Longitude" in data


def test_generate_caption():
    with open(SAMPLE_IMAGE, "rb") as f:
        response = client.post("/generate_caption/", files={"file": ("sample.jpg", f, "image/jpeg")})
    assert response.status_code == 200
    assert "caption" in response.json()


def test_auto_tag():
    with open(SAMPLE_IMAGE, "rb") as f:
        response = client.post("/auto_tag/", files={"file": ("sample.jpg", f, "image/jpeg")})
    assert response.status_code == 200
    assert "tags" in response.json()
    assert isinstance(response.json()["tags"], list)


def test_process_image():
    with open(SAMPLE_IMAGE, "rb") as f:
        response = client.post("/process_image", files={"file": ("sample.jpg", f, "image/jpeg")})
    assert response.status_code == 200
    data = response.json()
    assert "Caption" in data
    assert "AutoTags" in data
    assert "PersonLabel" in data
