import os, json, urllib.request

api_key = os.environ['NVIDIA_API_KEY']
base_url = "https://integrate.api.nvidia.com/v1/chat/completions"

headers = {
    "Authorization": f"Bearer {api_key}",
    "Content-Type": "application/json"
}

data = {
    "model": "qwen/qwen3-coder-480b-a35b-instruct",
    "messages": [
        {"role": "system", "content": os.getenv('SYSTEM_PROMPT_VAL', '')},
        {"role": "user", "content": os.getenv('USER_REQUEST_VAL', '')}
    ],
    "temperature": 0.2
}

req = urllib.request.Request(base_url, data=json.dumps(data).encode('utf-8'), headers=headers)
try:
    with urllib.request.urlopen(req) as response:
        res = json.loads(response.read().decode('utf-8'))
        enhanced_text = res['choices'][0]['message']['content']
        with open("enhanced_issue.txt", "w") as f:
            f.write(enhanced_text)
except Exception as e:
    print(f"Error: {e}")
    exit(1)
