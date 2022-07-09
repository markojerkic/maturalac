import { apps } from 'firebase-admin';
import { initializeApp, cert, ServiceAccount, App } from 'firebase-admin/app';
import { getFirestore } from 'firebase-admin/firestore';
import { getStorage } from 'firebase-admin/storage';
import firebaseConfig from '../../../firebase.config.json';
import env from '../../utils/env';

if (!apps.length) {
  initializeApp({
    credential: cert(firebaseConfig as ServiceAccount),
    databaseURL: env.FIRESTORE_URL,
    storageBucket: env.STORAGE_BUCKET
  });
}

const firestore = getFirestore();
const storage = getStorage().bucket();

export {firestore, storage};
