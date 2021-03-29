import { awsConfig, driftbottleConfig } from '../myConfig.js'
import '../css/driftbottle.css'
import Amplify from '@aws-amplify/core'
import Auth from '@aws-amplify/auth'

Amplify.configure(awsConfig)
Auth.configure(awsConfig)

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
    loadConversations()
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



// ------------------------------------------------------------------------------------------
// inbox
// ------------------------------------------------------------------------------------------
async function loadConversations() {
  // [
  //   {
  //     "id": "45d66579-f116-4e8d-a72b-751dd4ce8e14",
  //     "sender": "b68ce084-81fe-4854-b20c-d8118a8ba43a",
  //     "text": "Hi there!!",
  //     "timestamp": "2021-02-26T08:31:26Z"
  //   },{
  //   "id": "b511f496-429c-43e9-a57e-618f38a663cb",
  //   "sender": "fd17730f-f0b6-40bd-a18f-4d1116002b9b",
  //   "text": "I can help you.",
  //   "timestamp": "2021-02-28T11:49:16Z"
  // }
  // ]

  const session = await Auth.currentSession()
  const token = session.idToken.jwtToken
  const response = await fetch(`${driftbottleConfig.baseAPIUrl}/conversations`, {
    headers: {
      "Content-Type": "application/json",
      "Authorization": `Bearer ${token}`
    },
    method: "GET"
  })

  if (response.ok) {
    // get data
    const conversations = await response.json()

    // clear list
    const conversationTemplateElement = document.getElementById("conversationTemplate")
    const conversationListElement = document.getElementById("inbox").querySelector(".allConversations")
    while (conversationListElement.firstChild) {
      conversationListElement.removeChild(conversationListElement.lastChild);
    }
    conversationListElement.appendChild(conversationTemplateElement)

    // fill list
    conversations.forEach((conversation) => {
      const conversationElement = conversationTemplateElement.cloneNode(true)
      conversationElement.id = conversation.id
      conversationElement.querySelector("h5").html(conversation.sender)
      conversationElement.querySelector("p.card-text").firstChild.html(conversation.text)
      conversationElement.querySelector("p.card-text small").html(conversation.timestamp)
      conversationListElement.appendChild(conversationElement)
    })
  } else {
    console.error(response)
  }
}

// ------------------------------------------------------------------------------------------
//
// ------------------------------------------------------------------------------------------