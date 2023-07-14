import axios from "axios";

// create an axios instance
const api = axios.create({
  timeout: 60000, // request timeout
});

export default api;
