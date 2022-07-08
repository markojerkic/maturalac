import { initializeApp, cert, ServiceAccount } from 'firebase-admin/app';
import { getFirestore } from 'firebase-admin/firestore';
import firebaseConfig from '../../../firebase.config.json';

initializeApp({
  credential: cert(firebaseConfig as ServiceAccount),
  databaseURL: "https://drzavna-matura-1fbe7.firebaseio.com"
});

const firestore = getFirestore();

export default firestore;
