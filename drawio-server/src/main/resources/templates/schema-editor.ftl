<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Schema Editor</title>
    <!-- Link to Monaco Editor's CDN -->
    <script src="https://cdn.jsdelivr.net/npm/monaco-editor@0.41.0/min/vs/loader.js"></script>
    <style>
        #editor-container {
            width: 100%;
            height: 80vh;
            border: 1px solid #ccc;
            margin-top: 20px;
        }
        .button-container {
            margin-top: 10px;
        }
    </style>
</head>
<body>
    <h1>JSON Schema Editor</h1>
    <div id="editor-container"></div>
    <div class="button-container">
        <button id="save-btn">Save Schema</button>
        <span id="status-message" style="margin-left: 10px; color: green;"></span>
    </div>

    <script>
        let editor; // Declare the Monaco Editor instance globally

        // Configure Monaco Editor loader
        require.config({ paths: { 'vs': 'https://cdn.jsdelivr.net/npm/monaco-editor@0.41.0/min/vs' } });

        // Load Monaco Editor
        require(['vs/editor/editor.main'], function() {
            // Create the editor inside the container
            editor = monaco.editor.create(document.getElementById('editor-container'), {
                value: '', // JSON will be fetched dynamically
                language: 'json',
                automaticLayout: true
            });

            // Fetch the JSON schema on page load
            fetchSchema();
        });

        // Function to fetch JSON schema from GET /schema endpoint
        function fetchSchema() {
            fetch('/schema', {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Failed to fetch JSON schema');
                }
                return response.json();
            })
            .then(data => {
                // Set fetched JSON as editable content in Monaco Editor
                editor.setValue(JSON.stringify(data, null, 2));
            })
            .catch(error => {
                console.error('Error fetching JSON schema:', error);
                const statusMessage = document.getElementById('status-message');
                statusMessage.textContent = 'Failed to load schema';
                statusMessage.style.color = 'red';
            });
        }

        // Function to save the JSON schema using PUT /schema endpoint
        function saveSchema() {
            const updatedSchema = editor.getValue();

            try {
                // Verify if JSON is valid
                JSON.parse(updatedSchema);

                fetch('/schema', {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: updatedSchema
                })
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Failed to save schema');
                    }
                    return response.json();
                })
                .then(data => {
                    const statusMessage = document.getElementById('status-message');
                    statusMessage.textContent = 'Schema saved successfully!';
                    statusMessage.style.color = 'green';
                })
                .catch(error => {
                    console.error('Error saving JSON schema:', error);
                    const statusMessage = document.getElementById('status-message');
                    statusMessage.textContent = 'Failed to save schema';
                    statusMessage.style.color = 'red';
                });
            } catch (e) {
                alert('Invalid JSON. Please fix any syntax issues before saving.');
            }
        }

        // Attach click event listener to "Save" button
        document.getElementById('save-btn').addEventListener('click', saveSchema);
    </script>
</body>
</html>