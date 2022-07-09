import { z } from "zod";

const envValidator = z.object({
  FIRESTORE_URL: z.string(),
  STORAGE_BUCKET: z.string(),
});

const env = envValidator.safeParse(process.env);

if (!env.success) {
  console.error(
    '❌ Invalid environment variables:',
    JSON.stringify(env.error.format(), null, 4),
  );
  process.exit(1);
}
module.exports.env = env.data;
export default env.data;