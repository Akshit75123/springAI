// 1. Data and Configuration (Global Scope)
const modelMap = {
    openai: [
        { value: "gpt-5.4", text: "GPT-5.4" },
        { value: "gpt-5.4-mini", text: "GPT-5.4 Mini" }
    ],
    gemini: [
        { value: "gemini-2.5-flash", text: "Gemini 2.5 Flash" },
        // { value: "gemini-2-flash", text: "Gemini 2 Flash" }
    ]
};

// 2. Function to update the model dropdown based on provider
function updateModels() {
    const provider = document.getElementById('provider').value;
    const modelSelect = document.getElementById('model');
    
    modelSelect.innerHTML = "";

    modelMap[provider].forEach(model => {
        const opt = document.createElement('option');
        opt.value = model.value;
        opt.text = model.text;
        modelSelect.appendChild(opt);
    });
}

// 3. Initialize UI states
window.addEventListener('DOMContentLoaded', updateModels);
document.getElementById('provider').addEventListener('change', updateModels);

// 4. Main Streaming Logic
document.getElementById('startBtn').addEventListener('click', async function() {
    const provider = document.getElementById('provider').value;
    const model = document.getElementById('model').value;
    const message = document.getElementById('message').value;
    const responseArea = document.getElementById('responseArea');

    if (!message.trim()) {
        alert("Please enter a message!");
        return;
    }

    responseArea.innerText = "Thinking...";

    const url = `/stream?provider=${encodeURIComponent(provider)}&model=${encodeURIComponent(model)}&message=${encodeURIComponent(message)}`;

    try {
        const response = await fetch(url);
        const reader = response.body.getReader();
        const decoder = new TextDecoder();
        
        responseArea.innerText = ""; // Clear "Thinking..." once stream starts

        // Replace your while(true) loop with this:
let buffer = ''; 

while (true) {
    const { value, done } = await reader.read();
    if (done) break;

    buffer += decoder.decode(value, { stream: true });
    
    // Split by newlines to get individual SSE lines
    let lines = buffer.split('\n');
    
    // Keep the last partial line in the buffer
    buffer = lines.pop(); 

    for (let line of lines) {
    let trimmedLine = line.trim();
    
    if (trimmedLine.startsWith('data:')) {
        let content = line.substring(line.indexOf(':') + 1).trim();
        
        // Handle OpenAI format (which sends "[DONE]" at the end)
        if (content === "[DONE]") break;

        try {
            // Try to parse as JSON (OpenAI style)
            const json = JSON.parse(content);
            // OpenAI's structure in Spring AI usually maps to this:
            const text = json.choices[0].delta.content || "";
            responseArea.textContent += text;
        } catch (e) {
            // If it's not JSON, it's a raw string (Gemini style)
            // We use the untrimmed line to preserve spaces
            const rawContent = line.substring(line.indexOf(':') + 1);
            responseArea.textContent += rawContent;
        }
    }
}
    
    responseArea.scrollTop = responseArea.scrollHeight;
}

// Final flush for any remaining data
if (buffer.trim().startsWith('data:')) {
    responseArea.textContent += buffer.substring(buffer.indexOf(':') + 1);
}  } catch (err) {
        console.error("Fetch failed:", err);
        responseArea.innerText = "Error: Could not connect to the server.";
    }
});