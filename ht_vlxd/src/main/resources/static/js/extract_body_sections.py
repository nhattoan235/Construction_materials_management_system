import sys
import re

sys.stdout.reconfigure(encoding='utf-8')

with open(r"c:\Users\DUC_STRONG\Downloads\Doan_Java\Nhom1.txt", "r", encoding="utf-8") as f:
    content = f.read()

sections = [
    ("3.3.3.", "3.3.3. Giao diện quản lý đơn hàng"),
    ("3.3.4.", "3.3.4. Giao diện quản lý hợp đồng"),
    ("3.3.5.", "3.3.5. Giao diện quản lý thông tin cá nhân"),
    ("3.3.9.", "3.3.9. Giao diện quản lý đổi trả hàng"),
    ("3.3.10.", "3.3.10. Giao diện quản lý tài chính"),
    ("3.5.2.", "3.5.2. Chức năng mua hàng"),
    ("3.5.3.", "3.5.3. Chức năng tra cứu đơn"),
    ("3.5.4.", "3.5.4. Chức năng hủy đơn"),
    ("3.5.14.", "3.5.14. Chức năng cập nhật thông tin cá nhân"),
    ("3.5.36.", "3.5.36. Chức năng Đổi trả hàng"),
    ("3.5.37.", "3.5.37. Chức năng ghi nhận công nợ"),
    ("3.5.38.", "3.5.38. Chức năng thanh toán công nợ"),
]

print("--- DETAILED SECTION EXTRACTION ---")
for code, name in sections:
    # Look for the code in the text after position 20000 (where body starts)
    matches = [m for m in re.finditer(re.escape(code), content) if m.start() > 20000]
    if not matches:
        print(f"[{code}] {name} - NOT FOUND in body")
        continue
    
    # Take the first match in the body
    m = matches[0]
    start_pos = m.start()
    
    # Find next section or next header (like "3.3." or "3.5.") to determine end of content
    next_match = re.search(r"\d+\.\d+\.\d+\.", content[start_pos + len(code):])
    if next_match:
        end_pos = start_pos + len(code) + next_match.start()
    else:
        end_pos = start_pos + 1500  # Fallback to 1500 chars
        
    section_text = content[start_pos:end_pos].strip()
    print(f"=== {name} (Pos: {start_pos} to {end_pos}) ===")
    print(section_text)
    print("-" * 60)
