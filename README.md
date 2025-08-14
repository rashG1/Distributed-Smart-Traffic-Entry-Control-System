# NodePulse: Distributed Smart Traffic Entry Control System (Actor Model)

## Overview
NodePulse is a **scalable, decentralized smart traffic access control system** built using the **Actor Model** for high concurrency, modular scalability, and fault isolation.

It controls **city entry/exit points** using actor-based nodes equipped with sensors. Each actor operates independently, deciding in real time whether to allow or deny vehicle entry based on live capacity, priority rules, and special conditions.

---

## Why Actor Model?
The **Actor Model** enables:
- Independent state management for each entry point (boundary gate).
- High concurrency without centralized bottlenecks.
- Natural fault isolation — one gate’s failure does not affect others.
- Seamless scalability — new actors can be added anytime.

---

## Scalability Concept
The system is structured in **boundary circles** around the city.  
When the city expands or traffic grows:
- New **actor nodes** can be added to outer layers.
- Internal boundaries can be deployed for finer control (e.g., sensitive or VIP areas).

---

## Actor Use Cases
1. **Traffic Controller Actor** – Decides vehicle entry based on city capacity.
2. **Emergency Handler Actor** – Gives priority to emergency/priority vehicles.
3. **Exit Monitor Actor** – Tracks vehicles leaving to free up capacity.
4. **Load Balancer Actor** *(future)* – Redistributes traffic between entry points.

---

## Capabilities
**Current:**
- Boundary access control with live monitoring.
- Emergency vehicle prioritization.
- Real-time decentralized decision-making.
- Non-blocking concurrent operations.

**Future Possibilities:**
- AI-driven predictive traffic management.
- Integration with public transport scheduling.
- Multi-layer internal boundary control.

---

## Technical Overview
| Layer               | Technology          | Purpose |
|---------------------|---------------------|---------|
| Actor System        | Java with Akka      | Concurrency & state isolation |
| IoT Devices         | ESP32, Raspberry Pi | Sensor detection & communication |
| Messaging           | MQTT                 | Lightweight communication |
| Backend Panel       | Spring Boot          | Monitoring & admin control |
| Storage (optional)  | NoSQL DB             | Status & historical data |

---

## Example Flow
1. Vehicle approaches gate → Sensor sends ID to actor node.
2. Actor node checks capacity & priority rules.
3. Decision:
   - **Allow** if space available or priority vehicle.
   - **Deny** if full until capacity frees up.
4. Exit actors update live counts in real time.

---

## Proposal & Documentation
📄 [Read Full Proposal Here](#) *(Replace `#` with your link)*

---

## Contact
**Rashmika Rathnayaka**  
📧 rashmikarathnyaka01@gmail.com  
📞 +94 725 849 868
