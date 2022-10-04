import { storage } from ".";

const getFileDownloadLink = async (filename: string, isImage: boolean = true) =>
  (await storage.file(`${filename}.${isImage ? "png" : "mp3"}`))
    .getSignedUrl({
      version: "v4",
      action: "read",
      expires: Date.now() + 15 * 60 * 1000, // 15 minutes
    })
    .then((signedUrlResponse) => signedUrlResponse.toString());

export { getFileDownloadLink };
