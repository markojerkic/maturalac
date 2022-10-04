import dynamic from "next/dynamic";
import "swagger-ui-react/swagger-ui.css";

const SwaggerUI = dynamic(() => import("swagger-ui-react"), { ssr: false });

const Home = () => {
  // Serve Swagger UI with our OpenAPI schema
  return (
    <div className="h-full bg-white">
      <SwaggerUI url="/api/openapi.json" />
    </div>
  );
};

export default Home;
