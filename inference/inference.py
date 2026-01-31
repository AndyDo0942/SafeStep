import os
import cv2
import numpy as np
import torch
from segmentation import PotholeDetector 
from depth import DepthEstimator    

def run_pothole_analysis(image_path):
    detector = PotholeDetector()
    depth_tool = DepthEstimator()

    print(f"--- Processing: {os.path.basename(image_path)} ---")
    
    segment_result = detector.detect(image_path)
    depth_map = depth_tool.get_depth(image_path)
    h_depth, w_depth = depth_map.shape

    analysis_output = []

    if segment_result.masks is not None:
        masks = segment_result.masks.data.cpu().numpy()
        
        for i, mask in enumerate(masks):
            # 1. Align Mask
            mask_aligned = cv2.resize(mask, (w_depth, h_depth), interpolation=cv2.INTER_NEAREST).astype(np.uint8)
            
            # 2. CREATE THE "ROAD RING" (The Donut)
            # Dilate the pothole mask to get the area immediately around it
            kernel = np.ones((15, 15), np.uint8)
            dilated_mask = cv2.dilate(mask_aligned, kernel, iterations=2)
            road_ring_mask = dilated_mask - mask_aligned # Subtract the hole to get just the ring
            
            # 3. CALCULATE METRICS
            pothole_pixels = depth_map[mask_aligned > 0]
            road_pixels = depth_map[road_ring_mask > 0]
            
            if pothole_pixels.size > 0 and road_pixels.size > 0:
                # Use the simple median for both to get the most stable "plane-to-plane" distance
                road_surface_m = np.median(road_pixels)
                pothole_floor_m = np.median(pothole_pixels) 
                
                # The Delta
                true_depth_cm = (pothole_floor_m - road_surface_m) * 100
                
                # Logic guard: no negative depths (bumps)
                true_depth_cm = max(0, true_depth_cm)
                
                # Logic guard: if depth is negative, it's a bump, not a hole
                true_depth_cm = max(0, true_depth_cm)

                stats = {
                    "pothole_id": i,
                    "distance_to_road_m": round(float(road_surface_m), 2),
                    "estimated_depth_cm": round(float(true_depth_cm), 2),
                    "pixel_area": int(np.sum(mask_aligned > 0))
                }
                analysis_output.append(stats)
                print(f"  > Pothole {i}: Distance={stats['distance_to_road_m']}m, Depth={stats['estimated_depth_cm']}cm")
    
    return analysis_output

if __name__ == "__main__":
    BASE_DIR = os.path.dirname(__file__)
    TEST_IMG = os.path.join(BASE_DIR, "input", "pothole3.jpg")
    results = run_pothole_analysis(TEST_IMG)
    print("\n--- Final Results (Best Guess CM) ---")
    print(results)