pragma solidity ^0.8.4;

contract ETHEscrow {
    struct Payment {
        address payable sender;
        address payable receiver;
        uint256 amount;
        uint256 deadline;
        bool claimed;
        bool reverted;
        uint256 paymentId;
    }

    mapping(address => mapping(uint256 => Payment)) public sentPayments;
    mapping(address => mapping(uint256 => Payment)) public receivedPayments;
    
    Payment[] public payments;
    uint256 public currPaymentId = 0;

    event PaymentInitiated(uint256 payId, address sender, address receiver, uint256 amount, uint256 deadline);
    event PaymentClaimed(uint256 payId, address sender, address receiver, uint256 amount);
    event PaymentReverted(uint256 payId, address sender, address receiver, uint256 amount);

    function sendPayment(address payable _receiver, uint256 _timeAhead) external payable {
        require(msg.sender != _receiver, "Sender and receiver cannot be the same");
        uint256 deadline = block.timestamp + _timeAhead;
        require(deadline > block.timestamp, "Deadline must be in the future");
        require(msg.value > 0, "Payment amount must be greater than zero");

        currPaymentId++;
        Payment memory payment = Payment(payable(msg.sender), _receiver, msg.value, deadline, false, false, currPaymentId);
        sentPayments[msg.sender][currPaymentId] = payment;
        receivedPayments[_receiver][currPaymentId] = payment;
        payments.push(payment);

        emit PaymentInitiated(currPaymentId, msg.sender, _receiver, msg.value, deadline);
    }

    function claimPayment(uint256 _paymentId) external {
        Payment storage payment = receivedPayments[msg.sender][_paymentId];
        Payment storage payment_sender = sentPayments[payment.sender][_paymentId];
        require(payment.receiver == msg.sender, "Receiver can only claim the payment");
        require(!payment.claimed, "Payment has already been claimed");
        require(!payment.reverted, "Payment has been reverted by the sender");
        require(block.timestamp <= payment.deadline, "Payment deadline has passed");

        payment.claimed = true;
        payment_sender.claimed = true;
        payment.receiver.transfer(payment.amount);

        emit PaymentClaimed(_paymentId, payment.sender, payment.receiver, payment.amount);
    }

    function revertPayment(uint256 _paymentId) external {
        Payment storage payment = sentPayments[msg.sender][_paymentId];
        require(payment.sender == msg.sender, "Sender can only revert their own payments");
        require(!payment.claimed, "Payment has already been claimed");
        require(!payment.reverted, "Payment has already been reverted");

        payment.reverted = true;
        payment.sender.transfer(payment.amount);

        emit PaymentReverted(_paymentId, payment.sender, payment.receiver, payment.amount);
    }

    function getSentPayments(address _sender) external view returns (Payment[] memory) {
        Payment[] memory result = new Payment[](currPaymentId);
        uint256 count = 0;
        for (uint256 i = 1; i <= currPaymentId; i++) {
            if (sentPayments[_sender][i].sender == _sender) {
                result[count] = sentPayments[_sender][i];
                count++;
            }
        }
        return result;
    }

       function getReceivedPayments(address _receiver) external view returns (Payment[] memory) {
        Payment[] memory result = new Payment[](currPaymentId);
        uint256 count = 0;
        for (uint256 i = 1; i <= currPaymentId; i++) {
            if (receivedPayments[_receiver][i].receiver == _receiver) {
                result[count] = receivedPayments[_receiver][i];
                count++;
            }
        }
        return result;
    }
}