import torch
import torch.serialization
original_load = torch.load

def patched_load(f, map_location=None, pickle_module=None, **kwargs):
    kwargs['weights_only'] = False
    return original_load(f, map_location, pickle_module, **kwargs)

torch.load = patched_load
import os
from ultralyticsplus import YOLO, render_result

base_dir = os.path.dirname(__file__)

model = YOLO('keremberke/yolov8m-pothole-segmentation')

model.overrides['conf'] = 0.2
model.overrides['iou'] = 0.45
model.overrides['max_det'] = 1000
model.overrides['imgsz'] = 1280

image_path = os.path.join(base_dir, "input", "pothole4.jpg")

# Run inference
results = model.predict(image_path)

# View results
print(f"Detected {len(results[0].boxes)} potholes.")
render = render_result(model=model, image=image_path, result=results[0])
render.show()