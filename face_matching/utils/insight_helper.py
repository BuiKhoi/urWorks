import mxnet as mx
from sklearn import preprocessing
import numpy as np
import cv2


def get_embedding_model(model_str, layer, ctx=mx.cpu(), image_size=(112, 112)):
    _vec = model_str.split(',')
    prefix = _vec[0]
    epoch = int(_vec[1])
    print('loading', prefix, epoch)
    sym, arg_params, aux_params = mx.model.load_checkpoint(prefix, epoch)
    all_layers = sym.get_internals()
    sym = all_layers[layer+'_output']
    emb_model = mx.mod.Module(symbol=sym, context=ctx, label_names=None)
    emb_model.bind(
        data_shapes=[('data', (1, 3, image_size[0], image_size[1]))])
    emb_model.set_params(arg_params, aux_params)

    return emb_model


def get_feature(emb_model, aligned):
    input_blob = np.expand_dims(aligned, axis=0)
    data = mx.nd.array(input_blob)
    db = mx.io.DataBatch(data=(data,))
    emb_model.forward(db, is_train=False)
    embedding = emb_model.get_outputs()[0].asnumpy()
    embedding = preprocessing.normalize(embedding).flatten()

    return embedding

def get_embedding(nimg, embedding_model):
    nimg = cv2.cvtColor(nimg, cv2.COLOR_BGR2RGB)
    nimg = np.transpose(nimg, (2, 0, 1))
    embedding = get_feature(embedding_model, nimg).reshape(1, -1)

    return embedding

def CosineSimilarity(test_vec, source_vecs):
    """
    Verify the similarity of one vector to group vectors of one class
    """
    cos_dist = 0
    for source_vec in source_vecs:
        cos_dist += findCosineDistance(test_vec, source_vec)

    return cos_dist / len(source_vecs)

def findCosineDistance(vector1, vector2):
    """
    Calculate cosine distance between two vector
    """
    vec1 = vector1.flatten()
    vec2 = vector2.flatten()

    a = np.dot(vec1.T, vec2)
    b = np.dot(vec1.T, vec1)
    c = np.dot(vec2.T, vec2)

    return 1 - (a / (np.sqrt(b) * np.sqrt(c)))