from fastapi import FastAPI
import json
import urllib.request
import cv2

import sys
sys.path.insert(1, '/mnt/01D59EBC8D926700/Projects/urWorks/face_matching')

from match_faces import *

app = FastAPI()

@app.get('/test_api/')
def test_image(img, cmt):
    print(img)
    print(cmt)
    urllib.request.urlretrieve(img, "temp_face.jpg")
    urllib.request.urlretrieve(cmt, "temp_cmnd.jpg")

    face = cv2.imread('temp_face.jpg')
    cmnd = cv2.imread('temp_cmnd.jpg')

    emb2 = calculate_embedding(face)
    emb1 = calculate_embedding(cmnd)

    score = calculate_score(emb1, emb2)

    rtr_dict = {}
    rtr_dict['likely'] = round(score, 2)
    if score > COSINE_THRESHOLD:
        rtr_dict['approve'] = True
    else:
        rtr_dict['approve'] = False
    return rtr_dict

@app.get('/default_return/')
def default_return():
    return 'Default returning function'
