import cv2
import mediapipe as mp
import numpy as np
import os

kumpulan_gambar = {
    "jempol": "jempol.jpg",
    "berpikir": "berpikir.jpg",
    "biasa": "biasa.jpg",
    "tunjuk jari": "tunjuk jari.jpg"
}

mp_drawing = mp.solutions.drawing_utils
mp_hands = mp.solutions.hands
mp_face_mesh = mp.solutions.face_mesh

tangan = mp_hands.Hands(
    static_image_mode=False,
    max_num_hands=1,
    min_detection_confidence=0.7,
    min_tracking_confidence=0.5
)

jaring_muka = mp_face_mesh.FaceMesh(
    max_num_faces=1,
    min_detection_confidence=0.5,
    min_tracking_confidence=0.5
)

def load_and_resize_image(path, target_height):
    full_path = os.path.join(os.getcwd(), path)
    image = cv2.imread(full_path)
    if image is None:
        print(f"Error: Tidak bisa memuat gambar {path}. Pastikan file ada di direktori yang benar.")
        return None
    
    ratio = target_height / image.shape[0]
    target_width = int(image.shape[1] * ratio)
    return cv2.resize(image, (target_width, target_height))

def classify_gesture(hand_landmarks):
    y_thumb_tip = hand_landmarks.landmark[mp_hands.HandLandmark.THUMB_TIP].y
    y_index_tip = hand_landmarks.landmark[mp_hands.HandLandmark.INDEX_FINGER_TIP].y
    y_middle_tip = hand_landmarks.landmark[mp_hands.HandLandmark.MIDDLE_FINGER_TIP].y
    y_ring_tip = hand_landmarks.landmark[mp_hands.HandLandmark.RING_FINGER_TIP].y
    y_pinky_tip = hand_landmarks.landmark[mp_hands.HandLandmark.PINKY_TIP].y
    y_middle_pip = hand_landmarks.landmark[mp_hands.HandLandmark.MIDDLE_FINGER_PIP].y

    is_thumb_up = y_thumb_tip < y_middle_pip
    are_fingers_down = (y_index_tip > y_middle_pip and
                        y_middle_tip > y_middle_pip and
                        y_ring_tip > y_middle_pip and
                        y_pinky_tip > y_middle_pip
                        )
    
    if is_thumb_up and are_fingers_down:
        return "jempol"

    is_index_up = y_index_tip < y_middle_pip
    is_other_fingers_down = (y_middle_tip > y_middle_pip and
                              y_ring_tip > y_middle_pip and
                              y_pinky_tip > y_middle_pip
                            )
    is_thumb_down = y_thumb_tip > y_middle_pip

    if is_index_up and is_other_fingers_down and is_thumb_down:
        return "tunjuk jari"
    
    return "biasa"

def check_thinking_gesture(hand_landmarks, face_landmarks, frame_width, frame_height):
    if not hand_landmarks or not face_landmarks:
        return False

    index_tip = hand_landmarks.landmark[mp_hands.HandLandmark.INDEX_FINGER_TIP]
    index_x = int(index_tip.x * frame_width)
    index_y = int(index_tip.y * frame_height)
    nose_tip = face_landmarks.landmark[4]
    nose_x = int(nose_tip.x * frame_width)
    nose_y = int(nose_tip.y * frame_height)

    jarak = np.sqrt((index_x - nose_x) ** 2 + (index_y - nose_y) ** 2)
    MAX_JARAK = 60 

    y_middle_pip = hand_landmarks.landmark[mp_hands.HandLandmark.MIDDLE_FINGER_PIP].y
    y_middle_tip = hand_landmarks.landmark[mp_hands.HandLandmark.MIDDLE_FINGER_TIP].y
    is_middle_finger_down = y_middle_tip > y_middle_pip

    if jarak < MAX_JARAK and is_middle_finger_down:
        return True
    
    return False

INDEKS_KAMERA = 0
cap = cv2.VideoCapture(INDEKS_KAMERA)

print("Sedang menjalankan... Tekan 'q' atau 'Esc' untuk keluar.")

while cap.isOpened():
    sukses, frame = cap.read()
    if not sukses:
        print("Gagal membaca frame dari kamera. Menghentikan...")
        break

    frame = cv2.flip(frame, 1)
    frame_height, frame_width, _ = frame.shape
    rgb_frame = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
    hasil_tangan = tangan.process(rgb_frame)
    hasil_muka = jaring_muka.process(rgb_frame)

    gestur_saat_ini = "biasa"
    hand_landmarks_data = None
    face_landmarks_data = None

    if hasil_tangan.multi_hand_landmarks:
        hand_landmarks_data = hasil_tangan.multi_hand_landmarks[0]
    
    if hasil_muka.multi_face_landmarks:
        face_landmarks_data = hasil_muka.multi_face_landmarks[0]

    if hand_landmarks_data:
        if face_landmarks_data:
            if check_thinking_gesture(hand_landmarks_data, face_landmarks_data, frame_width, frame_height):
                gestur_saat_ini = "berpikir"
        
        if gestur_saat_ini == "biasa":
            gestur_saat_ini = classify_gesture(hand_landmarks_data)

        mp_drawing.draw_landmarks(
            frame,
            hand_landmarks_data,
            mp_hands.HAND_CONNECTIONS,
            mp_drawing.DrawingSpec(color=(0, 0, 255), thickness=2, circle_radius=6), 
            mp_drawing.DrawingSpec(color=(0, 255, 0), thickness=2, circle_radius=3)  
        )

    gambar_gestur = load_and_resize_image(kumpulan_gambar[gestur_saat_ini], frame_height)
    
    output_frame = frame

    if gambar_gestur is not None:
        output_frame = np.concatenate((frame, gambar_gestur), axis=1)
        cv2.putText(
            output_frame,
            f"Gestur: {gestur_saat_ini.replace('_', ' ')}",
            (frame_width + 10, 30), 
            cv2.FONT_HERSHEY_SIMPLEX,
            1,
            (0, 255, 0), 
            2
        )

    else:
        cv2.putText(
             output_frame,
             "Memuat gagal - Cek nama file secara teliti!",
             (10, 30), 
             cv2.FONT_HERSHEY_SIMPLEX,
             1,
             (0, 0, 255), 
             2
         )

    cv2.imshow("Deteksi Gestur Tangan", output_frame)
    
    key = cv2.waitKey(5)
    if key == ord('q') or key == 27:
        break

tangan.close()
jaring_muka.close()
cap.release()
cv2.destroyAllWindows()