class Rope(){
    // Number of monkeys waiting to cross the rope
    var n_waiting_east: Int = 0
    var n_waiting_west: Int = 0
    var n_crossing: Int = 0
    var direction_now: Int = 0
    var change_direction: Boolean = false

    def arrive(direction: Int){
        this.synchronized {
            var dir_str: String = ""

            // Direction of the rope
            if (n_crossing > 0 && direction != direction_now && !change_direction){
                change_direction = true
                println("Changing direction in order to avoid starvation")
            }
            if (n_crossing == 0 && n_waiting_east == 0 && n_waiting_west == 0){
                direction_now = direction
                println("----------------- Direction now settled to " + direction_now + " -----------------")
            }

            if (direction == 0){
                n_waiting_east += 1
                dir_str = "east"
            }
            else if (direction == 1){
                n_waiting_west += 1
                dir_str = "west"
            }

            println("Monkey " + Thread.currentThread().getName() + " wants to go to the " + dir_str + ". \t(" + n_waiting_east + "," + n_waiting_west + ") |")
        }
    }
    
    def cross(direction: Int){
        this.synchronized {
            while (direction_now != direction || (direction_now == direction && change_direction)){
                println("Monkey " + Thread.currentThread().getName() + " waiting for the rope to be empty. |")
                wait()
                Thread.sleep(1000)  // Minimum inter-monkey spacing is 1 second
            }

            n_crossing += 1

            if (direction == 0){
                n_waiting_east -= 1
            }
            else if (direction == 1){
                n_waiting_west -= 1 
            }

            println("Monkey " + Thread.currentThread().getName() + " is crossing the rope. \t(" + n_waiting_east + "," + n_waiting_west + ") | Crossing: (" + n_crossing + ")")
        }
    }

    def leave(){
        this.synchronized {
            n_crossing -= 1
            println("Monkey " + Thread.currentThread().getName() + " left the rope. \t\t(" + n_waiting_east + "," + n_waiting_west + ") | Crossing: (" + n_crossing + ")")

            // If no monkey is crossing
            if (n_crossing == 0){
                // Change direction to requested
                if (change_direction){
                    direction_now = (direction_now + 1) % 2
                    println("------------------ Direction now settled to " + direction_now + " -----------------")
                    change_direction = false
                }

                else if (n_waiting_east == 0 && n_waiting_west > 0){
                    direction_now = 1
                    println("----------------- Direction now settled to " + direction_now + " -----------------")
                }
                else if (n_waiting_east > 0 && n_waiting_west == 0){
                    direction_now = 0
                    println("----------------- Direction now settled to " + direction_now + " -----------------")
                }
                notifyAll()
            }
        }
    }

}
