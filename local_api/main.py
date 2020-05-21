from fastapi import FastAPI
import json
import urllib.request
import cv2
from keras.models import load_model
import numpy as np
import requests
import json
from keras.backend.tensorflow_backend import set_session
import tensorflow as tf
import random

import sys
sys.path.insert(1, '/mnt/01D59EBC8D926700/Projects/urWorks/face_matching')

from match_faces import *

app = FastAPI()
sess = tf.Session()
set_session(sess)
recommender_model = load_model('/mnt/01D59EBC8D926700/Projects/urWorks/recommender_system/checkpoints/recommender.h5')
graph = tf.get_default_graph()

hiring_firebase = 'https://hackathon-2020-9448d.firebaseio.com/data_matrix/hiring'
finding_firebase = 'https://hackathon-2020-9448d.firebaseio.com/data_matrix/finding'

def get_data_from_fb(firebase_link):
    # print(firebase_link)
    r = requests.get(firebase_link + '.json')
    return json.loads(r.content)

def decode_matrix(matrix, finding):
    if finding:
        decoded_matrix = []
        for i, m in enumerate(matrix.split('x')[0]):
            decoded_matrix.append(int(m))
        decoded_matrix.append(int(matrix.split('x')[-1]))
    else:
        decoded_matrix = []
        for i, m in enumerate(matrix):
            decoded_matrix.append(int(m))
        decoded_matrix.append(0)

    return np.array(decoded_matrix)

@app.get('/face_validate/')
def validate_face(img, cmt):
    mc = MxnetController()
    print(img)
    print(cmt)
    urllib.request.urlretrieve(img, "temp_face.jpg")
    urllib.request.urlretrieve(cmt, "temp_cmnd.jpg")

    face = cv2.imread('temp_face.jpg')
    cmnd = cv2.imread('temp_cmnd.jpg')

    emb2 = mc.calculate_embedding(face)
    emb1 = mc.calculate_embedding(cmnd)

    score = mc.calculate_score(emb1, emb2)

    rtr_dict = {}
    rtr_dict['likely'] = round(score, 2)
    if rtr_dict['likely'] > 100:
        rtr_dict['likely'] = 98
    if score > COSINE_THRESHOLD:
        rtr_dict['approve'] = True
    else:
        rtr_dict['approve'] = False
    return rtr_dict

@app.get('/get_recommended_posts')
def get_recommended_posts(userid, finding: bool):
    global graph
    global sess
    #Get user job matrix from firebase
    self_matrixes = []
    if finding:
        self_matrixes = get_data_from_fb(finding_firebase + '/' + userid)
    else:
        self_matrixes = get_data_from_fb(hiring_firebase + '/' + userid)
    self_matrixes = decode_matrix(self_matrixes, finding)
    #Get interested users job matrixes
    if finding:
        matrixes = get_data_from_fb(hiring_firebase)
    else: #if hiring
        matrixes = get_data_from_fb(finding_firebase)

    idxs = list(matrixes.keys())
    matrixes = list(matrixes.values())

    matrixes = np.array([decode_matrix(m, not finding) for m in matrixes])

    if finding:
        for i, m in enumerate(matrixes):
            print(m)
            matrixes[i] = self_matrixes - m
    else:
        for i, m in enumerate(matrixes):
            matrixes[i] = m - self_matrixes

    # print(matrixes)

    with graph.as_default():
        set_session(sess)
        results = recommender_model.predict(np.array(matrixes))
    print(max(results))
    # results = [r>0.5 for r in results]
    matched = [i for i, x in enumerate(results) if x > 0.5]
    matched = [idxs[ma] for ma in matched]
    return random.choices(matched, k=10)

@app.get('/default_return/')
def default_return():
    return 'Default returning function'