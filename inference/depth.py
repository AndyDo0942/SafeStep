import torch
from PIL import Image
import numpy as np
from transformers import pipeline

class DepthEstimator:
    def __init__(self):
        # CHANGED: Switched from Outdoor to Hypersim for better close-up scale
        model_id = "depth-anything/Depth-Anything-V2-Metric-Indoor-Large-hf"
        
        device = 0 if torch.cuda.is_available() else -1
        self.pipe = pipeline(
            task="depth-estimation", 
            model=model_id, 
            device=device
        )

    def get_depth(self, image_path):
        raw_img = Image.open(image_path)
        result = self.pipe(raw_img)
        # Predicted depth is in meters
        depth_map = result["predicted_depth"].numpy()
        return depth_map