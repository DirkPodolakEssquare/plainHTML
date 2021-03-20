import { aws_config } from '../myconfig.js'
import '../css/driftbottle.css'
import Amplify, { Auth } from 'aws-amplify'

Amplify.configure(aws_config)

const loginForm = document.getElementById("loginForm")
if (loginForm) {
  loginForm.onsubmit = async function loginFormSubmit(event) {
    event.preventDefault()
    const email = document.getElementById("userEmail").value
    const password = document.getElementById("userPassword").value

    try {
      const user = await Auth.signIn(email, password)

      console.log(user)

      location.href = "index2.html"
    } catch (e) {
      console.error(e)
    }
  }
}


const registrationForm = document.getElementById("registrationForm")
if (registrationForm) {
  registrationForm.onsubmit = async function registrationFormSubmit(event) {
    event.preventDefault()
    const email = document.getElementById("userEmail").value
    const password = document.getElementById("userPassword").value
    const passwordRepetition = document.getElementById("userPasswordRepetition").value

    if (password !== passwordRepetition) {
      console.error("password and password repetition do not match")
      return;
    }

    try {
      const { user } = await Auth.signUp({
        username: email,
        password: password,
        attributes: {
          email: email
        }
      });
      console.log(user);

      location.href = "confirmRegistration.html"
    } catch (error) {
      console.log('error signing up:', error);
    }
  }
}


const verificationForm = document.getElementById("verificationForm")
if (verificationForm) {
  verificationForm.onsubmit = async function verificationFormSubmit(event) {
    event.preventDefault()
    const email = document.getElementById("userEmail").value
    const verificationCode = document.getElementById("verificationCode").value

    try {
      const user = await Auth.confirmSignUp(email, verificationCode);
      console.log(user);
      location.href = "index2.html"
    } catch (error) {
      console.log('error signing up:', error);
    }
  }
}







const page = window.location.pathname.split("/").pop()

console.log(page)

if ("index2.html" === page) {
  handleIndex2()
}

async function handleIndex2() {
  try {
    const user = await Auth.currentAuthenticatedUser({bypassCache: true})
    console.log(user)
    console.log(`user ${user.attributes.email} is logged in`)

    document.getElementById("username").textContent = user.attributes.email
  } catch (e) {
    console.log("no user logged in")
  }
}
















// ------------------------------------------------------------------------------------------
const test = async () => {
  console.log("init")

  console.log(document.getElementById("username"))


  try {
    const user = await Auth.currentAuthenticatedUser({bypassCache: true})
    console.log(user)
    console.log(`user ${user.attributes.email} is logged in`)

    document.getElementById("username").textContent = user.attributes.email
  } catch (e) {
    console.log("no user logged in")

    const TEMP_PASSWORD = "password1234"
    const NEW_PASSWORD = "password1234"

    const user = await Auth.signIn("dirk.podolak.essquare@gmail.com", TEMP_PASSWORD)
    if (user.challengeName === 'NEW_PASSWORD_REQUIRED') {
      await Auth.completeNewPassword(user, NEW_PASSWORD)
    }
  }
}

// ------------------------------------------------------------------------------------------



async function signUp() {
  try {
    const { user } = await Auth.signUp({
      username,
      password,
      attributes: {
        email,          // optional
        phone_number,   // optional - E.164 number convention
        // other custom attributes
      }
    });
    console.log(user);
  } catch (error) {
    console.log('error signing up:', error);
  }
}


//    "webpack": "^4.17.1",