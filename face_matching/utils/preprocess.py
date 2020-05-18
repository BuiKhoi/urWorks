import cv2
import numpy as np
from skimage import transform as trans

def preprocess(img, bbox=None, landmark=None, **kwargs):

    M = None
    image_size = []
    str_image_size = kwargs.get("image_size", "")

    if len(str_image_size) > 0:
        image_size = [int(x) for x in str_image_size.split(",")]
        if len(image_size) == 1:
            image_size = [image_size[0], image_size[0]]

    if landmark is not None:

        src = np.array(
            [
                [30.2946, 51.6963],
                [65.5318, 51.5014],
                [48.0252, 71.7366],
                [33.5493, 92.3655],
                [62.7299, 92.2041],
            ],
            dtype=np.float32,
        )
        if image_size[1] == 112:
            src[:, 0] += 8.0
        dst = landmark.astype(np.float32)

        tform = trans.SimilarityTransform()
        tform.estimate(dst, src)
        M = tform.params[0:2, :]

    if M is None:
        if bbox is None:  # use center crop
            det = np.zeros(4, dtype=np.int32)
            det[0] = int(img.shape[1] * 0.0625)
            det[1] = int(img.shape[0] * 0.0625)
            det[2] = img.shape[1] - det[0]
            det[3] = img.shape[0] - det[1]
        else:
            det = bbox
        margin = kwargs.get("margin", 44)
        bb = np.zeros(4, dtype=np.int32)
        bb[0] = np.maximum(det[0] - margin / 2, 0)
        bb[1] = np.maximum(det[1] - margin / 2, 0)
        bb[2] = np.minimum(det[2] + margin / 2, img.shape[1])
        bb[3] = np.minimum(det[3] + margin / 2, img.shape[0])
        ret = img[bb[1] : bb[3], bb[0] : bb[2], :]
        if len(image_size) > 0:
            ret = cv2.resize(ret, (image_size[1], image_size[0]))
        return ret
    else:  # do align using landmark
        warped = cv2.warpAffine(img, M, (image_size[1], image_size[0]), borderValue=0.0)

        return warped

def format_landmarks(landmark):
    landmarks = np.array(
        [
            landmark["left_eye"][0],
            landmark["right_eye"][0],
            landmark["nose"][0],
            landmark["mouth_left"][0],
            landmark["mouth_right"][0],
            landmark["left_eye"][1],
            landmark["right_eye"][1],
            landmark["nose"][1],
            landmark["mouth_left"][1],
            landmark["mouth_right"][1],
        ]
    )
    landmarks = landmarks.reshape((2, 5)).T

    return landmarks