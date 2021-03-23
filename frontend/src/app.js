import {aws_config} from '../myconfig.js'
import '../css/driftbottle.css'
import Amplify from '@aws-amplify/core'
import Auth from '@aws-amplify/auth'

Amplify.configure(aws_config)
Auth.configure(aws_config)

// ------------------------------------------------------------------------------------------
// authentication
// ------------------------------------------------------------------------------------------

initAuthentication()

async function initAuthentication() {
  if (await userIsLoggedIn()) {
    console.log("logged in")
    console.log(await getUser())

    document.getElementById("login").style.display = "none"
    document.getElementById("signup").style.display = "none"
    document.getElementById("logout").style.display = "flex"
    document.getElementById("authentication").style.display = "none"
    document.getElementById("inbox").style.display = "flex"

    registerLogoutHandler()
  } else {
    console.log("not logged in")

    document.getElementById("login").style.display = "flex"
    document.getElementById("signup").style.display = "flex"
    document.getElementById("logout").style.display = "none"
    document.getElementById("inbox").style.display = "none"

    registerLoginHandler()
    registerSignupHandler()
    registerVerificationCodeHandler()
  }
}

async function getUser() {
  try {
    return await Auth.currentAuthenticatedUser({bypassCache: true})
  } catch (e) {
    console.log(e)
  }
}

async function userIsLoggedIn() {
  try {
    const user = await getUser()
    return !!user
  } catch (e) {
    return false
  }
}

function registerLogoutHandler() {
  document.getElementById("logout").onclick = async function (event) {
    event.preventDefault()

    console.log("logout...")
    try {
      await Auth.signOut()
    } catch (e) {
      console.error(e)
    }
    console.log("...logged out")

    location.reload()
  }
}

function registerLoginHandler() {
  document.getElementById("login").onclick = async function (event) {
    event.preventDefault()

    document.getElementById("authenticationForm").onsubmit = async function loginFormSubmit(event) {
      event.preventDefault()

      const email = document.getElementById("userEmail").value
      const password = document.getElementById("userPassword").value

      try {
        await Auth.signIn(email, password)
        location.reload()
      } catch (e) {
        console.error(e)
      }
    }

    document.getElementById("authenticationCardHeader").innerText = "Login"
    document.getElementById("authenticationPassword").style.display = "block"
    document.getElementById("authenticationRepeatPassword").style.display = "none"
    document.getElementById("authenticationVerificationCode").style.display = "none"
    document.getElementById("authenticationSubmit").textContent = "login now"
    document.getElementById("showVerificationCode").style.display = "block"
    document.getElementById("authentication").style.display = "block"
  }
}

function registerSignupHandler() {
  document.getElementById("signup").onclick = async function (event) {
    event.preventDefault()

    document.getElementById("authenticationForm").onsubmit = async function registrationFormSubmit(event) {
      event.preventDefault()

      const email = document.getElementById("userEmail").value
      const password = document.getElementById("userPassword").value
      const passwordRepetition = document.getElementById("userPasswordRepetition").value

      if (password !== passwordRepetition) {
        console.error("password and password repetition do not match")
        return
      }

      try {
        await Auth.signUp({
          username: email,
          password: password,
          attributes: {
            email: email
          }
        })

        //location.href = "confirmRegistration.html"
        location.reload()
      } catch (error) {
        console.log('error signing up:', error)
      }
    }

    document.getElementById("authenticationCardHeader").innerText = "Registration"
    document.getElementById("authenticationPassword").style.display = "block"
    document.getElementById("authenticationRepeatPassword").style.display = "block"
    document.getElementById("authenticationVerificationCode").style.display = "none"
    document.getElementById("authenticationSubmit").textContent = "register now"
    document.getElementById("showVerificationCode").style.display = "block"
    document.getElementById("authentication").style.display = "block"
  }
}

function registerVerificationCodeHandler() {
  document.getElementById("showVerificationCode").onclick = async function (event) {
    event.preventDefault()

    document.getElementById("authenticationForm").onsubmit = async function registrationFormSubmit(event) {
      event.preventDefault()

      const email = document.getElementById("userEmail").value
      const verificationCode = document.getElementById("verificationCode").value

      await Auth.confirmSignUp(email, verificationCode);
      location.reload()
    }

    document.getElementById("authenticationCardHeader").innerText = "Verification"
    document.getElementById("authenticationPassword").style.display = "none"
    document.getElementById("authenticationRepeatPassword").style.display = "none"
    document.getElementById("authenticationVerificationCode").style.display = "block"
    document.getElementById("showVerificationCode").style.display = "none"
    document.getElementById("authenticationSubmit").textContent = "submit verification code"
    document.getElementById("authentication").style.display = "block"
  }
}

// ------------------------------------------------------------------------------------------
// /authentication
// ------------------------------------------------------------------------------------------