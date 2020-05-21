from utils.config import *
from utils.insight_helper import *
from mtcnn import MTCNN
import cv2
from utils.preprocess import *
import time

class MxnetController:
    def __init__(self):
        self.detector = MTCNN()
        self.embedding_model = self.get_embedding_model(INSIGHT_MODEL_PATH, "fc1")
        print('Embedding model loaded')

    def calculate_score(self, emb1, emb2):
        total_score = CosineSimilarity(emb1, emb2)
        total_score -= MIN_COSINE_SIMILARITY
        total_score /= (MAX_COSINE_SIMILARITY - MIN_COSINE_SIMILARITY)
        total_score *= 100
        total_score = 100 - total_score
        return total_score

    def calculate_embedding(self, image):
        #detect for face
        detect_result = self.detector.detect_faces(image)
        for result in detect_result:
            # print(type(result))
            if result['confidence'] > DETECTION_SCORE_THRESHOLD:
                # print(result)
                bbox = np.array(result['box'])
                bbox[bbox < 0] = 0
                
                landmarks = format_landmarks(result['keypoints'])
                image = preprocess(image, bbox, landmarks, image_size=IMAGE_SIZE_STR)
                embedding = self.get_embedding(image)
        return embedding

    def get_embedding(self, nimg):
        nimg = cv2.cvtColor(nimg, cv2.COLOR_BGR2RGB)
        nimg = np.transpose(nimg, (2, 0, 1))
        embedding = self.get_feature(self.embedding_model, nimg).reshape(1, -1)

        return embedding

    def get_feature(self, emb_model, aligned):
        input_blob = np.expand_dims(aligned, axis=0)
        data = mx.nd.array(input_blob)
        db = mx.io.DataBatch(data=(data,))
        emb_model.forward(db, is_train=False)
        embedding = emb_model.get_outputs()[0].asnumpy()
        embedding = preprocessing.normalize(embedding).flatten()

        return embedding

    def get_embedding_model(self, model_str, layer, ctx=mx.cpu(), image_size=(112, 112)):
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

def main():
    # time.sleep(5)
    img = cv2.imread('./huy1.jpg')
    img2 = cv2.imread('./huy2.jpg')

    mc = MxnetController()

    emb2 = mc.calculate_embedding(img2)
    emb1 = mc.calculate_embedding(img)

    print(mc.calculate_score(emb1, emb2))

if __name__ == '__main__':
    main()