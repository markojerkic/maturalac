import { generateOpenApiDocument } from "trpc-openapi";
import { appRouter } from ".";

const openApiDocument = generateOpenApiDocument(appRouter, {
  title: 'tRPC OpenAPI',
  version: '1.0.0',
  tags: ['auth', 'users', 'posts'],
  baseUrl: 'http://localhost:3000/api'
});

export default openApiDocument;