---
description: Run the mock backend server
---

To run the mock backend server for the application:

1.  Make sure you have json-server installed globally or locally.
    ```bash
    npm install -g json-server
    ```

2.  Run the server using the db.json file created in the project root.
    ```bash
// turbo
    json-server --watch db.json --port 3000
    ```
