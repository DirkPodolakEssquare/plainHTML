import { awsConfig, driftbottleConfig } from '../myConfig.js'
import '../css/driftbottle.css'
import Amplify from '@aws-amplify/core'
import Auth from '@aws-amplify/auth'
import { onAuthUIStateChange } from '@aws-amplify/ui-components'
import { applyPolyfills, defineCustomElements } from '@aws-amplify/ui-components/loader'

initAuthentication()

// ------------------------------------------------------------------------------------------
// authentication
// ------------------------------------------------------------------------------------------
async function initAuthentication() {

  // const awsConfig = {
  //   "aws_project_region": "eu-central-1",
  //   "aws_cognito_region": "eu-central-1",
  //   "aws_user_pools_id": "eu-central-1_1yLRgkbyK",
  //   "aws_user_pools_web_client_id": "6cjv2hbrolg3h07thapbq12csu",
  //   "oauth": {
  //     domain: "driftbottle.auth.eu-central-1.amazoncognito.com",
  //     scope: ["http://driftbottle.eu-central-1.elasticbeanstalk.com/api/conversation.read"],
  //     // redirectSignIn: "https://driftbottlefrontendstack-driftbottlefrontendbucke-1gp5phfwh1z3n.s3.eu-central-1.amazonaws.com/",
  //     redirectSignIn: "http://localhost:8081/",
  //     redirectSignOut: "",
  //     responseType: 'code' // or 'token', note that REFRESH token will only be generated when the responseType is code
  //   }
  // }

  Amplify.configure(awsConfig)
  Auth.configure(awsConfig)

  applyPolyfills().then(() => {
    defineCustomElements(window)
  })

  if (await userIsLoggedIn()) {
    initUserLoggedIn()
  } else {
    initUserNotLoggedIn()
  }

  let authUIStateChangedToSignedInCounter = 0
  onAuthUIStateChange((authState, authData) => {
    // TODO: I don't know why this is called twice, so I workaround double calls with a counter
    if ("signedin" === authState) {
      if (authUIStateChangedToSignedInCounter === 0) {
        initUserLoggedIn()
        authUIStateChangedToSignedInCounter += 1
      }
    } else {
      initUserNotLoggedIn()
    }
  })
}

async function initUserLoggedIn() {
  document.getElementById("inbox").style.display = "flex"
  document.getElementsByTagName("body")[0].style.background = "none"

  loadConversations()
}

async function initUserNotLoggedIn() {
  document.getElementById("inbox").style.display = "none"
  document.getElementsByTagName("body")[0].style.backgroundImage = "url('images/message-in-a-bottle.png')"
  document.getElementsByTagName("body")[0].style.backgroundPosition = "center"
  document.getElementsByTagName("body")[0].style.backgroundRepeat = "no-repeat"
  document.getElementsByTagName("body")[0].style.backgroundSize = "cover"
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
// ------------------------------------------------------------------------------------------
// /authentication
// ------------------------------------------------------------------------------------------



// ------------------------------------------------------------------------------------------
// inbox
// ------------------------------------------------------------------------------------------
async function loadConversations() {
  console.log("loading conversations")

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
   // const token = session.idToken.jwtToken
  const token = session.accessToken.jwtToken
console.log("token")
console.log(token)
  try {
    const response = await fetch(`${driftbottleConfig.baseAPIUrl}/conversations`, {
      headers: {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${token}`
      },
      method: "GET"
    })
    console.log("response")
    console.log(response)

    if (response.ok) {
      // get data
      const conversations = await response.json()
      console.log("conversations")
      console.log(conversations)

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
  } catch (e) {
    console.error(e)
  }
}

// ------------------------------------------------------------------------------------------
//
// ------------------------------------------------------------------------------------------