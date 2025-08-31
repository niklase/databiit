# Run locally

Pull docker image

    
    % docker pull mongo:latest 

Start mongodb in Docker

   
    % docker run -d --name mongodb-container -p 27017:27017 -e MONGO_INITDB_ROOT_USERNAME=admin -e MONGO_INITDB_ROOT_PASSWORD=adminpassword mongo    

Test application by calling REST API


    