import dynamic from "next/dynamic";
import "swagger-ui-react/swagger-ui.css";

const SwaggerUI = dynamic(() => import("swagger-ui-react"), { ssr: false });

const Home = () => {
  // Serve Swagger UI with our OpenAPI schema
  return (
    <div className="bg-white h-full">
      <SwaggerUI url="/api/openapi.json" />
    </div>
  );
};

export default Home;
