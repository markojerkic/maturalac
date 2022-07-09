import { apps } from 'firebase-admin';
import { initializeApp, cert, ServiceAccount, App } from 'firebase-admin/app';
import { getFirestore } from 'firebase-admin/firestore';
import { getStorage } from 'firebase-admin/storage';
import firebaseConfig from '../../../firebase.config.json';

if (!apps.length) {
  initializeApp({
    credential: cert(firebaseConfig as ServiceAccount),
    databaseURL: process.env.FIRESTORE_URL,
    storageBucket: process.env.STORAGE_BUCKET
  });
}

const firestore = getFirestore();
const storage = getStorage().bucket();

export {firestore, storage};
