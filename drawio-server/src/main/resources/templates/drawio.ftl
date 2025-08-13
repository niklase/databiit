<!DOCTYPE html>
<html>
<head>
  <title>Draw.io Embed</title>
</head>
<body>
  <iframe id="drawioFrame" src="https://embed.diagrams.net/?embed=1&proto=json&autosave=1" width="100%" height="800"></iframe>

  <script>
    const frame = document.getElementById('drawioFrame');

    // Event listener to process iframe messages
    window.addEventListener('message', async function (winEvent) {
        console.log('Received event:', winEvent);

        // Verify origin of iframe communication
        if (winEvent.origin === 'https://embed.diagrams.net') {
            let data;

            // Parse incoming data safely
            try {
                data = typeof winEvent.data === 'string' ? JSON.parse(winEvent.data) : winEvent.data;
            } catch (error) {
                console.error('Error parsing event data:', error);
                return;
            }

            console.log('Parsed data:', data);

            // Check for init action
            if (data.event === 'init') {
                console.log('Draw.io editor initialized');

                // Load a diagram into the iframe
                const diagram = await fetch('/load-diagram').then(res => res.text());
                frame.contentWindow.postMessage(JSON.stringify({
                    action: 'load',
                    autosave: 1,
                    xml: diagram
                }), 'https://embed.diagrams.net');
            }

            // Handle save events
            if (data.event === 'autosave') {
                console.log('Autosave event received');
            }

            // Handle save events
            if (data.event === 'save' || data.event === 'autosave') {
                console.log('Save event received');
                await fetch('/save-diagram', {
                    method: 'POST',
                    headers: { 'Content-Type': 'text/plain' },
                    body: data.xml
                });

                frame.contentWindow.postMessage(JSON.stringify({ action: 'saveDone' }), 'https://embed.diagrams.net');
            }
        } else {
            console.warn('Message received from untrusted origin:', winEvent.origin);
        }
    }, false);
  </script>
</body>
</html>