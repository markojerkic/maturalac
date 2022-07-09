import { generateOpenApiDocument } from 'trpc-openapi';
import { appRouter } from '.';
import { getBaseUrl } from '../../utils/trpc';

const openApiDocument = generateOpenApiDocument(appRouter, {
  title: 'tRPC OpenAPI',
  version: '1.0.0',
  tags: ['auth', 'users', 'posts'],
  baseUrl: `${getBaseUrl()}/api`,
});

export default openApiDocument;
