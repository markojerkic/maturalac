import { storage } from "./firebase"

const getFileDownloadLink = async (filename: string) => {
  return await storage.file(filename).getSignedUrl({action: 'read', expires: '03-09-2491'});
}

export { getFileDownloadLink };