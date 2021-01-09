# UPISmartIntent
UPI Smart Intent sample application code.

Summary:
- The sample app displays various buttons to handle UPI intent in a msart manner
- The first button invokes geenric OS intent app chooser where Android OS lists all apps that can handle UPI intent
- The subsequent specific UPI app buttons invoke the specific apps to handle the UPI intent payment
- The smartness here is that the specific app buttons are visible only when the specific UPI app is both installed and has a UPI ready user avaialable
    - UPI readiness is as per the NPCI circular: https://www.npci.org.in/PDF/npci/upi/circular/2019/Circular-73-Payer_App_behaviour_for_Intent_based_transaction_on_UPI.pdf
  
