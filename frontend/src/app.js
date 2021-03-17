import '../css/driftbottle.css'
import Amplify, { Auth } from 'aws-amplify'

const aws_data = {
  "aws_project_region": "eu-central-1",
  "aws_cognito_region": "eu-central-1",
  "aws_user_pools_id": "eu-central-1_XtznInn0X",
  "aws_user_pools_web_client_id": "4sthaucoul7j03proj2i48rdhi",
  "oauth": {}
}

Amplify.configure(aws_data)

// ------------------------------------------------------------------------------------------
const init = async () => {
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

init()
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