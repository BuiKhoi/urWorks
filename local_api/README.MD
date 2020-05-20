# File tasks:
    main - Main funciton of uvicorn

# Done tasks:
    - Predicting function ready
    - Collecting data function ready


# Todo:
    - New file with configuration for api, no need to modify main.py each modification

# Tutorial:
## Run API
    Setup firewall:
        sudo apt install ufw
        sudo ufw allow 22, 8888, 8000, 5900 <- These are the ports you are using
    Start API: uvicorn --port 8000 --host 0.0.0.0 main:app --reload
    * Where 8000 is your port