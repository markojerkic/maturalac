import { apps } from 'firebase-admin';
import {
  initializeApp, cert, ServiceAccount, App,
} from 'firebase-admin/app';
import { getFirestore } from 'firebase-admin/firestore';
import { getStorage } from 'firebase-admin/storage';
import { getAnalytics } from 'firebase/analytics';
import env from '../../utils/env';

const firebaseConfig = {
  type: env.type,
  project_id: env.project_id,
  private_key_id: env.private_key_id,
  private_key: env.private_key,
  client_email: env.client_email,
  elient_id: env.client_id,
  auth_uri: env.auth_uri,
  token_uri: env.token_uri,
  auth_provider_x509_cert_url: env.auth_provider_x509_cert_url,
  client_x509_cert_url: env.client_x509_cert_url,
};

if (!apps.length) {
  initializeApp({
    credential: cert(firebaseConfig as ServiceAccount),
    databaseURL: env.FIRESTORE_URL,
    storageBucket: env.STORAGE_BUCKET,
  });
}

const firestore = getFirestore();
const storage = getStorage().bucket();
// TODO: Add analytics
// const analytics = getAnalytics();

export { firestore, storage };
