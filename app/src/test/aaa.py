import json, base64, io
from PIL import Image
import numpy as np

with open('app/src/main/assets/welcome.json', 'r', encoding='utf-8') as f:
    data = json.load(f)

b64 = data['assets'][0]['p'].split(',')[1]
img_bytes = base64.b64decode(b64)
img = Image.open(io.BytesIO(img_bytes)).convert('RGBA')
arr = np.array(img, dtype=np.float32)

rgb = arr[:, :, :3]
alpha = arr[:, :, 3]

# Compute luminance / brightness
# Pure black is 0..8
max_c = np.max(rgb, axis=2)

# Create transparency mask based on brightness:
# If max_c <= 6: alpha = 0 (completely transparent)
# If 6 < max_c < 24: smooth fade from 0 to original alpha
# If max_c >= 24: keep original alpha
smooth_alpha = np.clip((max_c - 5.0) / (22.0 - 5.0), 0.0, 1.0)
new_alpha = alpha * smooth_alpha

# Also feather the edges (top, bottom, left, right) over 25 pixels
h, w = arr.shape[:2]
feather = 30
edge_x = np.ones((h, w), dtype=np.float32)
edge_y = np.ones((h, w), dtype=np.float32)

for x in range(feather):
    factor = (x / float(feather)) ** 1.5
    edge_x[:, x] = factor
    edge_x[:, w - 1 - x] = factor

for y in range(feather):
    factor = (y / float(feather)) ** 1.5
    edge_y[y, :] = factor
    edge_y[h - 1 - y, :] = factor

edge_mask = edge_x * edge_y
final_alpha = np.clip(new_alpha * edge_mask, 0.0, 255.0).astype(np.uint8)

arr[:, :, 3] = final_alpha
res_img = Image.fromarray(arr.astype(np.uint8))

# Save on a test background matching MicrobondDarkBackground (11, 20, 26)
test_bg = Image.new('RGBA', (w, h), (11, 20, 26, 255))
test_bg.paste(res_img, (0, 0), res_img)
test_bg.save('c:/Users/raynz/AndroidStudioProjects/microbond/scratch_test_on_theme.png')
res_img.save('c:/Users/raynz/AndroidStudioProjects/microbond/scratch_test_transparent.png')


import json, base64, io
from PIL import Image
import numpy as np

with open('app/src/main/assets/welcome.json', 'r', encoding='utf-8') as f:
    data = json.load(f)

b64 = data['assets'][0]['p'].split(',')[1]
img_bytes = base64.b64decode(b64)
img = Image.open(io.BytesIO(img_bytes)).convert('RGB')
arr = np.array(img, dtype=np.float32)

rgb = arr[:, :, :3]
# Compute alpha from brightness (0..255)
# Using max channel
alpha = np.max(rgb, axis=2)

# Soft edge feather (30px on left, right, top, bottom)
h, w = arr.shape[:2]
feather_x = 35
feather_y = 35
edge_mask = np.ones((h, w), dtype=np.float32)

for x in range(feather_x):
    f = (x / float(feather_x)) ** 1.5
    edge_mask[:, x] *= f
    edge_mask[:, w - 1 - x] *= f

for y in range(feather_y):
    f = (y / float(feather_y)) ** 1.5
    edge_mask[y, :] *= f
    edge_mask[h - 1 - y, :] *= f

# Also smooth out any noise near pure black (<= 3)
clean_alpha = np.clip((alpha - 3.0) / (255.0 - 3.0), 0.0, 1.0) * 255.0
final_alpha = np.clip(clean_alpha * edge_mask, 0.0, 255.0)

# Create RGBA
rgba = np.zeros((h, w, 4), dtype=np.uint8)
rgba[:, :, :3] = np.clip(rgb, 0, 255).astype(np.uint8)
rgba[:, :, 3] = final_alpha.astype(np.uint8)

res_img = Image.fromarray(rgba, 'RGBA')
res_img.save('c:/Users/raynz/AndroidStudioProjects/microbond/scratch_test_additive_rgba.png')

# Composite on MicrobondDarkBackground (11, 20, 26)
bg = Image.new('RGBA', (w, h), (11, 20, 26, 255))
comp = Image.alpha_composite(bg, res_img)
comp.save('c:/Users/raynz/AndroidStudioProjects/microbond/scratch_test_additive_comp.png')


import json, base64, io
from PIL import Image
import numpy as np

with open('app/src/main/assets/welcome.json', 'r', encoding='utf-8') as f:
    data = json.load(f)

b64 = data['assets'][0]['p'].split(',')[1]
img_bytes = base64.b64decode(b64)
img = Image.open(io.BytesIO(img_bytes)).convert('RGB')
arr = np.array(img, dtype=np.float32)

rgb = arr[:, :, :3]
alpha = np.max(rgb, axis=2)

h, w = arr.shape[:2]
feather_x = 35
feather_y = 35
edge_mask = np.ones((h, w), dtype=np.float32)

for x in range(feather_x):
    f = (x / float(feather_x)) ** 1.5
    edge_mask[:, x] *= f
    edge_mask[:, w - 1 - x] *= f

for y in range(feather_y):
    f = (y / float(feather_y)) ** 1.5
    edge_mask[y, :] *= f
    edge_mask[h - 1 - y, :] *= f

clean_alpha = np.clip((alpha - 3.0) / (255.0 - 3.0), 0.0, 1.0) * 255.0
final_alpha = np.clip(clean_alpha * edge_mask, 0.0, 255.0)

rgba = np.zeros((h, w, 4), dtype=np.uint8)
rgba[:, :, :3] = np.clip(rgb, 0, 255).astype(np.uint8)
rgba[:, :, 3] = final_alpha.astype(np.uint8)

res_img = Image.fromarray(rgba, 'RGBA')
buf = io.BytesIO()
res_img.save(buf, format='WEBP', quality=95)
b64_out = base64.b64encode(buf.getvalue()).decode('utf-8')
print("Original size:", len(b64), "New size:", len(b64_out))



