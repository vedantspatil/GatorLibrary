import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

//Main class to run the program
public class gatorTaxi {
	public static void main(String[] args) throws IOException {

		// setting a flag 'terminate' to terminate code when duplicate ride is found
		boolean terminate = false;
		List<String> wholeInput = new ArrayList<>();
		String input;
		RedBlackTree rbtObj = new RedBlackTree();
		// setting the path of the output file
		try {
			// File file = new File("input2.txt");
			File file = new File(args[0]);
			BufferedReader br = new BufferedReader(new FileReader(file));
			String st;
			while ((st = br.readLine()) != null)
				wholeInput.add(st);
			br.close();
		} catch (Exception e) {
			System.out.println("Error: " + e);
		}
		
		// processing the input
		for (String s : wholeInput) {
			input = s;
			String[] arr = input.split("\\(");
			input = arr[0];
			switch (input) {
				case "PrintBook":
					String[] bookIds = arr[1].split("\\)");
					// searching the tree for the rideNumber in the input
					TreeNode book = rbtObj.search(Integer.parseInt(bookIds[0]));
					// if no such ride is found, print(0,0,0)
					if (book == null) {
						System.out.println("Book "+ bookIds[0] + " not found in the Library\n");
					} else {
						// printing the ride details of the found ride
						rbtObj.printNode(book);
					}
					break;
				case "PrintBooks":
					bookIds = arr[1].split("\\)")[0].split(",");
					// printing the ride details of the rides found in the given range
					ArrayList<TreeNode> nodesInRange = rbtObj.findNodesInRange(rbtObj.getRoot(), Integer.parseInt(bookIds[0]),
							Integer.parseInt(bookIds[1]));
					// if no rides are found in the given range
					// Change
					if (nodesInRange.size() == 0) {
						System.out.println("No books are found in the given range");
					}
					else{
						for(TreeNode node: nodesInRange){
							rbtObj.printNode(node);
						}
					}
					break;

				case "InsertBook":
					String[] bookDetails = arr[1].split("\\)")[0].split(",");
					book = rbtObj.search(Integer.parseInt(bookDetails[0]));
					
					// if ride already exists for the given ride number
					if (book != null) {
						System.out.println("Duplicate Book");
						break;
					} else {
						// inserting ride into Red Black Tree
						rbtObj.insert(Integer.parseInt(bookDetails[0]),
									 bookDetails[1],
									 bookDetails[2],
									 bookDetails[3]
									 );

					}
					break;

				case "BorrowBook":
					bookDetails = arr[1].split("\\)")[0].split(",");
					book = rbtObj.search(Integer.parseInt(bookDetails[1].trim()));
					//book doesnt exist
					if (book == null) {
						System.out.println("Book does not exist");
						break;
					}
					//if book availabile

					if(book.availability.toString().trim().equals("Yes")){
						book.availability = "No";
						book.borrowedBy = bookDetails[0];
						System.out.println("Book borrowed by Patron "+ bookDetails[0]);
					}else{
						UserNode newPatron = new UserNode(bookDetails[0],
											 Integer.parseInt(bookDetails[2]), 
											new Timestamp(System.currentTimeMillis()));
						book.reservationHeap.add(newPatron);
						System.out.println("Book "+ book.bookId +" Reserved by Patron " + bookDetails[0]);
					}

					break;

				case "ReturnBook":
					bookDetails = arr[1].split("\\)")[0].split(",");
					book = rbtObj.search(Integer.parseInt(bookDetails[1].trim()));
					if (book == null) {
						System.out.println("Book does not exist");
						break;
					}
					System.out.println("Book " + bookDetails[1] + " returned by patron "+ bookDetails[0]);
					//if book availabile
			
					if(book.reservationHeap.isEmpty()){
						book.availability = "Yes";
						book.borrowedBy = "None";
					}else{
						UserNode newPatron = book.reservationHeap.poll();
						book.borrowedBy = newPatron.userId;
						System.out.println("Book "+ book.bookId +" allocated to Patron " + newPatron.userId);
					}

					break;

				case "DeleteBook":
					String bookId = arr[1].split("\\)")[0];
					book = rbtObj.search(Integer.parseInt(bookId.trim()));
					if (book == null) {
						System.out.println("Book does not exist");
						break;
					}

					String displayString = "Book "+bookId+" is no longer available. ";
					if (book.reservationHeap.size()>=1){
						if(book.reservationHeap.size()>1){
							displayString+="Reservations made by Patrons ";
							for(UserNode node: book.reservationHeap){
								displayString+=node.userId+" ";
							}
							displayString+="have been cancelled!";
						}else{
							displayString+="Reservation made by Patron "+ book.reservationHeap.poll().userId + " have been cancelled!";
						}
					}
					rbtObj.deleteNode(Integer.parseInt(bookId));
					System.out.println(displayString);
					break;
				
				case "FindClosestBook":
					bookId = arr[1].split("\\)")[0];
					ArrayList<Integer> arrayList = rbtObj.printClosest(Integer.parseInt(bookId));
					for(int book_id: arrayList){
						TreeNode fNode = rbtObj.search(book_id);
						rbtObj.printNode(fNode);
					}
					break;

				case "ColorFlipCount":
					System.out.println(rbtObj.getRoot().bookId);
					break;

				// 	// searching the red black tree using ride number
				// 	ride = rbtObj.search(rideNo);

				// 	// if no such ride found, move to next input
				// 	if (ride == null)
				// 		break;
				// 	// case 1 of update trip scenario
				// 	if (new_duration <= ride.tripDuration) {
				// 		// delete the existing ride
				// 		HeapNode delNode4 = minHeap.deleteKey(ride.node.idx);
				// 		rbtObj.deleteNode(delNode4.rbt.rideNumber);

				// 		// insert new ride with updated ride cost
				// 		HeapNode insertedNode = minHeap.insert(new HeapNode(rideNo, delNode4.rideCost, new_duration));
				// 		insertedNode.rbt = rbtObj.insert(insertedNode.rideNumber, insertedNode.rideCost,
				// 				insertedNode.tripDuration, insertedNode);
				// 		// case 2 of update trip scenario
				// 	} else if ((ride.tripDuration < new_duration) && (new_duration <= (2 * ride.tripDuration))) {

				// 		int newCost = ride.rideCost + 10;

				// 		// delete the existing ride
				// 		HeapNode delNode2 = minHeap.deleteKey(ride.node.idx);
				// 		rbtObj.deleteNode(delNode2.rbt.rideNumber);

				// 		// insert new ride with updated ride cost
				// 		HeapNode insertedNode = minHeap.insert(new HeapNode(rideNo, newCost, new_duration));
				// 		insertedNode.rbt = rbtObj.insert(insertedNode.rideNumber, insertedNode.rideCost,
				// 				insertedNode.tripDuration, insertedNode);
				// 		// case 3 of update trip scenario
				// 	} else if (new_duration > (2 * ride.tripDuration)) {

				// 		// delete the existing ride from min heap and red black tree
				// 		HeapNode delNode3 = minHeap.deleteKey(ride.node.idx);
				// 		rbtObj.deleteNode(delNode3.rbt.rideNumber);
				// 	}
				// 	break;

			}
			// terminating the program in case of inserting a duplicate ride
			if (terminate)
				break;
		}

	}
}