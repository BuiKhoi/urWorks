from utils.config import *
from utils.insight_helper import *
from mtcnn import MTCNN
import cv2
from utils.preprocess import *
import time

detector = MTCNN()
embedding_model = get_embedding_model(INSIGHT_MODEL_PATH, "fc1")
print('Embedding model loaded')

def calculate_score(emb1, emb2):
    total_score = CosineSimilarity(emb1, emb2)
    total_score -= MIN_COSINE_SIMILARITY
    total_score /= (MAX_COSINE_SIMILARITY - MIN_COSINE_SIMILARITY)
    total_score *= 100
    total_score = 100 - total_score
    return total_score

def calculate_embedding(image):
    #detect for face
    detect_result = detector.detect_faces(image)
    for result in detect_result:
        # print(type(result))
        if result['confidence'] > DETECTION_SCORE_THRESHOLD:
            # print(result)
            bbox = np.array(result['box'])
            bbox[bbox < 0] = 0
            
            landmarks = format_landmarks(result['keypoints'])
            image = preprocess(image, bbox, landmarks, image_size=IMAGE_SIZE_STR)
            embedding = get_embedding(image, embedding_model)
            # cv2.imshow('image', image)
            # if cv2.waitKey(0) & 0xFF == ord('q'):
            #     break
            # print(embedding.shape)
    return embedding

def main():
    # time.sleep(5)
    img = cv2.imread('./vh1.jpg')
    img2 = cv2.imread('./st1.jpg')
    emb2 = calculate_embedding(img2)
    emb1 = calculate_embedding(img)

    print(calculate_score(emb1, emb2))

if __name__ == '__main__':
    main()