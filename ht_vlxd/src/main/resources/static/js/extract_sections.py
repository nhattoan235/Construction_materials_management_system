import sys
import re

with open(r"c:\Users\DUC_STRONG\Downloads\Doan_Java\Nhom1.txt", "r", encoding="utf-8") as f:
    content = f.read()

sys.stdout.reconfigure(encoding='utf-8')

sections = [
    ("3.3.3", "3.3.3. Giao diện quản lý đơn hàng"),
    ("3.3.4", "3.3.4. Giao diện quản lý hợp đồng"),
    ("3.3.5", "3.3.5. Giao diện quản lý thông tin cá nhân"),
    ("3.3.9", "3.3.9. Giao diện quản lý đổi trả hàng"),
    ("3.3.10", "3.3.10. Giao diện quản lý tài chính"),
    ("3.3.11", "3.3.11. Giao diện quản lý kho"),
]

for kw, name in sections:
    print(f"=== SEARCHING FOR: {name} ===")
    matches = list(re.finditer(re.escape(kw), content, re.IGNORECASE))
    if not matches:
        print("Not found")
        continue
    for match in matches:
        start = max(0, match.start() - 50)
        end = min(len(content), match.end() + 2000)
        print(f"Position {match.start()}:\n{content[start:end]}\n")
        print("="*80)
