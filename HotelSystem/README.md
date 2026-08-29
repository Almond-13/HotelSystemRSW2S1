Hotel Management System
Introduction

A Java-based Hotel Management System developed using Object-Oriented Programming (OOP) and Abstract Data Types (ADTs). The system manages guest registration, room allocation, booking, check-in/check-out, VIP guests, loyalty tiers, and housekeeping operations.

Modules:
1. Walk-In Registration
The Walk-In Registration module manages guests who arrive without prior bookings.
ADT Used: CircularArrayQueue

Main functions:
1. Register Guest
2. Update Guest Information
3. Cancel Guest (which is after register the guest want to cancel their booking before assign room)
4. Assign Room
5. heck-In
6. Check-Out
7. Generate Reports

---------------------------------------------------------------------------------------------------
2. VIP & Loyalty Tier Priority Room Allocation Module
Manages VIP guests and prioritises room allocation based on their loyalty tier and reward points.
ADT Used: VipPriorityQueue

Main functions:
1. Register VIP Member
2. Add VIP Allocation Request
3. Prioritise VIP Guests
4. Allocate Rooms
5. Search and Cancel VIP Requests
6. VIP and Room Reports

---------------------------------------------------------------------------------------------------
3. Loyalty Rewards
Manages guest loyalty accounts, reward points, and membership tiers.
ADT Used: ArrayList

Main functions:
1. Create Loyalty Account
2. Add / Redeem Points
3. View Member
4. Tier Report
6. Expiring Points Reports

---------------------------------------------------------------------------------------------------
4. Housekeeping Management
Manages room cleaning status and housekeeping history.
ADT Used: LinkedStack

Main functions:
1. Update Room Status
2. View Status History
3. Rollback Room Status
4. Generate Room Status Reports
5. Generate Rollback Frequency Reports

=====================================================================================================
Process:
1. Register(Walk-In Module & VIP Module & Layalty Module)
2. Assign Room (Walk-In Module & VIP Module)
3. Redeem Point (Layalty Module)
4. Check In/ Check Out (Walk-In Module)
5. HouseKeeping Cleaning "Dirty Room" (Housekeeping Module)
6. Updated All Record.

