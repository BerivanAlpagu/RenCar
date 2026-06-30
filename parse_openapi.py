import json

with open("app/src/main/java/com/turkcell/rencar/docs/api/openapi.json") as f:
    data = json.load(f)

if "swaggerDoc" in data:
    data = data["swaggerDoc"]

print("=== PATHS ===")
for path, methods in data.get("paths", {}).items():
    for method, info in methods.items():
        print(f"[{method.upper()}] {path} - {info.get('summary', '')}")

print("\n=== COMPONENTS (SCHEMAS) ===")
schemas = data.get("components", {}).get("schemas", {})
for name, schema in schemas.items():
    print(f"\n{name}:")
    properties = schema.get("properties", {})
    if properties:
        for prop_name, prop_info in properties.items():
            print(f"  - {prop_name}: {prop_info.get('type', 'ref')}")
    else:
        print("  - Empty or No Properties")
