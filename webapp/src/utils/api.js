/**
 *
 * @param {*} endpoint : relative URI path that will be used by the proxy to call the right operation on ressource(s)
 * @param {*} method : HTTP method to call the right operation (CRUD, Create = POST, Read = GET ...)
 * @param {*} token : JWT token to be provided when an operation on a ressource is secured
 * @param {*} data : data to be sent in the body of the request (for POST, PUT... requests)
 */
import Navbar from "../Components/Navbar.js";
import {resetCurrentUser, removeSessionData} from "./session.js";
import LoginRegisterPage from "../Components/LoginRegisterPage.js";

async function callAPI(endpoint, method = "get", token, data) {
  let headers = new Headers();
  let options = {};
  options.method = method;

  if (token) {    
    headers.append("Authorization", token);   
  }

  if (data) {
    options.body = JSON.stringify(data);
  }

  if (
    method.toLowerCase() === "post" ||
    method.toLowerCase() === "patch" ||
    method.toLowerCase() === "delete" ||
    method.toLowerCase() === "put"
  )
    headers.append("Content-Type", "application/json");
  options.headers = headers;
  try {
    const response = await fetch(endpoint, options);
    if (!response.ok) {    
      const error = await response.text(); // get the textual error message
      if (error === ("Expired token")) {
        resetCurrentUser();
        removeSessionData();
        await Navbar();
        setTimeout(LoginRegisterPage, 2000);
        alert("Veuillez vous reconnecter")
      }
      throw new Error(error);
    }
    return await response.json();
  } catch (error) {
    console.log("error:", error);
    throw error;
  }
}

export default callAPI;
