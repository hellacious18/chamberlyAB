# 🌙 Dream Weaver

**Use the power of AI to interpret your dreams, uncover hidden emotions, and track your mental landscape through a personalized, daily dream journal.**

Dream Weaver is an Android application designed to help users **log**, **reflect**, and **share** their dreams. It features personalized dream journaling, AI-powered dream interpretations, and a supportive community where users can engage with each other. **Firebase** powers the backend for secure authentication and real-time data management.

---

## ✨ Features

### 🔐 Sign-In Activity
- **Google Sign-In Integration** via Firebase Authentication.
- Secure and seamless user login experience.

---

### 💬 Fragment 1: Dream Chat (Gemini AI)
- Integrated **Google Gemini API** to interpret and discuss dreams.
- Custom context setup for meaningful dream-related interactions.
- Users can:
  - Send and receive messages with Gemini.
  - Clear individual conversations.
  - Delete entire chat histories.

> **Firestore Path:**  
> `dreamlogs -> {user_email} -> messages -> {chat_data}`

---

### 📔 Fragment 2: Personal Dream Logs
- Users can:
  - Select a specific **date**.
  - Add **title** and **content** for each dream.
  - Edit or delete their past dream entries.
- Dreams are stored persistently in **Firebase Firestore**.

> **Firestore Path:**  
> `userdreams -> {user_email} -> dreams -> {date} -> {title, content}`

---

### 🌐 Fragment 3: Dream Sharing Community
- Share dreams **publicly** with the Dream Weaver community.
- View and interact with other users’ dreams:
  - Like and comment on shared posts.
  - Post authors can delete their own posts.
  - Users can delete their own comments.
- Real-time interactions powered by **Firebase Realtime Database**.

> **Realtime DB Path:**  
> `posts -> {post_id} -> {dreamtext, likes, comments}`

---

### 👤 Fragment 4: Profile & Settings
- View basic profile info:
  - **Profile Picture**
  - **Display Name**
  - **Email**
- Perform account actions:
  - **Log Out**
  - **Delete Account** (removes all associated data from Firestore/Realtime DB)

---

## 🛠 Built With
- **Android** (Kotlin)
- **Firebase Authentication**
- **Firebase Firestore**
- **Firebase Realtime Database**
- **Google Gemini API** (REST/SDK)
- **Material Design UI**

---

## 📦 Download & Resources

- 🔽 [Download APK] https://drive.google.com/drive/folders/1nmR0JpMrTgXBTGIP5EIBpwPlf4eINubM?usp=drive_link
- 📄 [Ideation & Reference Docs] https://drive.google.com/drive/folders/1nmR0JpMrTgXBTGIP5EIBpwPlf4eINubM?usp=drive_link

> *Note: You may need to enable installation from unknown sources on your Android device.*

