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
    debugger
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
  const session = await Auth.currentSession()
  const token = session.accessToken.jwtToken

  try {
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
        conversationElement.querySelector("h5").textContent = conversation.sender
        conversationElement.querySelector("p.card-text").textContent = conversation.text
        conversationElement.querySelector("p.card-text small").textContent = conversation.timestamp
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