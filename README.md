# Oversimplication
Bob is new to crypto world. He wants to do all the amazing things like play games in web3, send any amount of money to anyone accross the world etc, access web3 apps. But he doesn't understand why everything in crypto is so complex. He gets to know about our app coinease. With CoinEase built on top of conflux ecosystem, he can scan a QR Code and tokens will be transferred. If he is worried he may send tokens to wrong address, we got him covered. He can revert his transactions too! Also, not to mention, he can use google accounts and phone numbers to access this wallet.

He can also buy CFX from anyone around the world, trustlessly! And he can sell any excess CFX too, without going all technical!

# How to run

EthEscrow.sol deployed on
0xaD7243c1a5d8F32F843B2d75075E6466d1d23d93

TokenTransfer.sol deployed on
0xA8785B7219610F99Ac974DB85e55C87e1aab34BA

To build android app, clone repository and open CoinEase folder
run commands

```
./gradlew build
adb install -r your_app.apk
```

To build "Particle Contract Calls" or "Token Transfer" backend, open the folders and run commands

```
npm install
npm start
```

backend deployed on https://http-nodejs-vkx8.vercel.app

# Architecture

![image](https://github.com/raehat/coin_ease/assets/77321971/e9c2fdc0-443c-420d-bb2d-9c9acc5d0347)

![Group 2 (3)](https://github.com/raehat/coin_ease/assets/77321971/57165b03-a437-45e9-ae58-eac959f42b45)

# Vision
Onboarding new users to conflux ecosystem

# Description
CoinEase

Our project aims to provide a user-friendly and secure platform for individuals entering the conflux ecosystem. Leveraging the Particle Network for seamless onboarding through various authentication methods such as Google accounts, phone numbers, and social media accounts, we empower beginners to engage with blockchain technology effortlessly.

# Key Features:

Escrow Service with Cross-Chain Functionality: Our app offers an escrow service allowing users to securely transfer funds within conflux ecosystem. This feature is particularly beneficial for beginners who may inadvertently send tokens to incorrect addresses. Users can revert their transactions before they are claimed by the receiver, mitigating the risk of irreversible mistakes. Additionally, we have implemented QR code-based token transfers. We perform document verification and e-kyc to make sure legit users onboard BUT WE DO NOT STORE ANY INFORMATION ANYWHERE!

Trustless Buy/Sell Crypto with Fiat: To facilitate the acquisition of CFX tokens for users onboarded via Particle Network, our platform offers a trustless buy/sell crypto service using fiat currency. Users can easily purchase native tokens from their bank accounts, providing a seamless transition into the world of cryptocurrencies. Here's how it works:

Seller (Bob) Creates Order: Bob initiates an order by locking his tokens in the smart contract, indicating his intention to sell.
Buyer (Alice) Places Order: Alice browses through the available orders and chooses to buy tokens from Bob. She confirms the transaction, becoming the sender.
Payment Verification: Alice proceeds to make the payment through a payment gateway like Razorpay. Our system verifies the completion of the payment via the Razorpay API backend.
Token Release: Upon successful payment verification, the corresponding tokens are automatically released to Alice's wallet address.
Reversion Mechanism: If Alice fails to complete the payment within the specified timeframe (e.g., 15 minutes), Bob's tokens are reverted back to him. This ensures fairness and prevents potential losses for the seller.
Automated and Trustless Process: Unlike traditional exchanges where transactions rely on manual confirmation from both parties, our platform operates on a fully automated and trustless basis. Transactions are executed seamlessly without the need for user intervention, providing a hassle-free experience for both buyers and sellers.

Conclusion: By integrating the Particle Network for user onboarding and implementing advanced features such as escrow services and trustless buy/sell crypto with fiat for conflux ecosystem, our app aims to democratize access to cryptocurrencies and streamline the user experience for newcomers. We prioritize security, automation, and simplicity to empower individuals entering the web3 ecosystem, driving widespread adoption and innovation in the decentralized finance (DeFi) space.
