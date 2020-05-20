from fastapi import FastAPI
import json
import urllib.request
import cv2
from keras.models import load_model
import numpy as np
import requests
import json

import sys
sys.path.insert(1, '/mnt/01D59EBC8D926700/Projects/urWorks/face_matching')

from match_faces import *

app = FastAPI()
recommender_model = load_model('/mnt/01D59EBC8D926700/Projects/urWorks/recommender_system/checkpoints/recommender.h5')

hiring_firebase = 'https://hackathon-2020-9448d.firebaseio.com/data_matrix/hiring'
finding_firebase = 'https://hackathon-2020-9448d.firebaseio.com/data_matrix/finding'

def get_data_from_fb(firebase_link):
    r = requests.get(firebase_link + '.json')
    return json.loads(r.content)

def decode_matrix(matrix, finding):
    if finding:
        decoded_matrix = np.zeros(len(matrix.split('x')[0]))
        for i, m in enumerate(decoded_matrix):
            m[i] = int(matrix[i])
        decoded_matrix[-1] = int(matrix.split('x')[-1])
    else:
        decoded_matrix = np.zeros(len(matrix))
        for i, d in enumerate(decode_matrix):
            d[i] = int(matrix[i])

    return decoded_matrix
    

@app.get('/face_validate/')
def validate_face(img, cmt):
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

@app.get('/get_recommended_posts')
def get_recommended_posts(userid, finding):
    #Get user job matrix from firebase
    self_matrixes = []
    if finding:
        self_matrixes = get_data_from_fb(finding_firebase + '/' + userid)
    else:
        self_matrixes = get_data_from_fb(hiring_firebase + '/' + userid)
    self_matrixes = decode_matrix(self_matrixes, finding)
    print(self_matrixes)
    return
    #Get interested users job matrixes
    matrixes = []
    idxs = []
    if finding:
        pass
    else: #if hiring
        pass

    if finding:
        for m in matrixes:
            m = self_matrixes - m
    else:
        for m in matrixes:
            m = m - self_matrixes

    results = recommender_model.predict(np.array(matrixes))
    # results = [r>0.5 for r in results]
    matched = [i for i, x in enumerate(results) if x > 0.5]

    return idxs[matched]

@app.get('/default_return/')
def default_return():
    return 'Default returning function'