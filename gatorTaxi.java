import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//Main class to run the program
public class gatorTaxi {
	public static void main(String[] args) throws IOException {

		// setting a flag 'terminate' to terminate code when duplicate ride is found
		boolean terminate = false;
		int ColorFlipCount = 0;
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
		try {
			for (String s : wholeInput) {
			input = s;
			String[] arr = input.split("\\(");
			input = arr[0].trim();
			switch (input) {
				case "PrintBook":
					String[] bookIds = arr[1].split("\\)");
					// searching the tree for the rideNumber in the input
					TreeNode book = rbtObj.search(Integer.parseInt(bookIds[0].trim()));
					// if no such ride is found, print(0,0,0)
					if (book == null) {
						System.out.println("Book "+ bookIds[0].trim() + " not found in the Library\n");
					} else {
						// printing the ride details of the found ride
						rbtObj.printNode(book);
					}
					break;
				case "PrintBooks":
					bookIds = arr[1].split("\\)")[0].split(",");
					// printing the ride details of the rides found in the given range
					ArrayList<TreeNode> nodesInRange = rbtObj.findNodesInRange(rbtObj.getRoot(), Integer.parseInt(bookIds[0]),
							Integer.parseInt(bookIds[1].trim()));
					// if no rides are found in the given range
					// Change
					if (nodesInRange.size() == 0) {
						System.out.println("No books are found in the given range\n");
					}
					else{
						for(TreeNode node: nodesInRange){
							rbtObj.printNode(node);
						}
					}
					break;

				case "InsertBook":
					HashMap<Integer, Integer> oldTree = new HashMap<>();
					rbtObj.inOrderTraversalofTree(rbtObj.getRoot(), oldTree);

					String[] bookDetails = arr[1].split("\\)")[0].split(",");
					book = rbtObj.search(Integer.parseInt(bookDetails[0].trim()));
					
					// if ride already exists for the given ride number
					if (book != null) {
						System.out.println("Duplicate Book\n");
						break;
					} else {
						// inserting ride into Red Black Tree
						rbtObj.insert(Integer.parseInt(bookDetails[0].trim()),
									 bookDetails[1],
									 bookDetails[2],
									 bookDetails[3]
									 );

					}
					HashMap<Integer, Integer> newTree = new HashMap<>();
					rbtObj.inOrderTraversalofTree(rbtObj.getRoot(), newTree);

					for (Map.Entry<Integer,Integer> entry : newTree.entrySet()){
						if(oldTree.containsKey(entry.getKey())){
							if(oldTree.get(entry.getKey()) != entry.getValue()){
								ColorFlipCount++;
							}
						}
					}
					break;

				case "BorrowBook":
					bookDetails = arr[1].split("\\)")[0].split(",");
					book = rbtObj.search(Integer.parseInt(bookDetails[1].trim()));
					//book doesnt exist
					if (book == null) {
						System.out.println("Book does not exist\n");
						break;
					}
					//if book availabile
					if(book.availability.toString().trim().equals(new String("\"Yes\""))){
						book.availability = "No";
						book.borrowedBy = bookDetails[0];
						System.out.println("Book "+ bookDetails[1].trim()+ " Borrowed by Patron "+ bookDetails[0].trim()+"\n");
					}else{
						UserNode newPatron = new UserNode(bookDetails[0].trim(),
											 Integer.parseInt(bookDetails[2].trim()), 
											new Timestamp(System.currentTimeMillis()));
						book.reservationHeap.add(newPatron);
						System.out.println("Book "+ book.bookId +" Reserved by Patron " + bookDetails[0]+"\n");
					}

					break;

				case "ReturnBook":
					bookDetails = arr[1].split("\\)")[0].split(",");
					book = rbtObj.search(Integer.parseInt(bookDetails[1].trim()));

					
					if (book == null) {
						System.out.println("Book does not exist\n");
						break;
					}
					System.out.println("Book " + bookDetails[1].trim() + " Returned by patron "+ bookDetails[0]+ "\n");
					//if book availabile

					if(book.reservationHeap.isEmpty()){
						book.availability = "\"Yes\"";
						book.borrowedBy = "None";
					}else{
						UserNode newPatron = book.reservationHeap.poll();
						book.borrowedBy = newPatron.userId;
						System.out.println("Book "+ book.bookId +" Allocated to Patron " + newPatron.userId +"\n");
					}

					break;

				case "DeleteBook":
					oldTree = new HashMap<>();
					rbtObj.inOrderTraversalofTree(rbtObj.getRoot(), oldTree);

					String bookId = arr[1].split("\\)")[0];
					book = rbtObj.search(Integer.parseInt(bookId.trim()));
					if (book == null) {
						System.out.println("Book does not exist\n");
						break;
					}

					String displayString = "Book "+bookId+" is no longer available.";
					if (book.reservationHeap.size()>=1){
						if(book.reservationHeap.size()>1){
							displayString+="Reservations made by Patrons ";
							for(UserNode node: book.reservationHeap){
								displayString+=node.userId+" ";
							}
							displayString+="have been cancelled!.";
						}else{
							displayString+="Reservation made by Patron "+ book.reservationHeap.poll().userId + " have been cancelled!";
						}
					}
					displayString+="\n";
					System.out.println(displayString);
					rbtObj.deleteNode(Integer.parseInt(bookId.trim()));

					newTree = new HashMap<>();
					rbtObj.inOrderTraversalofTree(rbtObj.getRoot(), newTree);
					for (Map.Entry<Integer,Integer> entry : newTree.entrySet()){
						if(oldTree.containsKey(entry.getKey())){
							if(oldTree.get(entry.getKey()) != entry.getValue()){
								ColorFlipCount++;
							}
						}
					}
					break;

				case "FindClosestBook":
					bookId = arr[1].split("\\)")[0];
					ArrayList<Integer> arrayList = rbtObj.printClosest(Integer.parseInt(bookId.trim()));
					for(int book_id: arrayList){
						TreeNode fNode = rbtObj.search(book_id);
						rbtObj.printNode(fNode);
					}
					break;

				case "ColorFlipCount":
					System.out.println("Color Flip Count: "+ ColorFlipCount+"\n");;
					break;

				case "Quit":
					System.out.println("Program Terminated!!\n");
					System.exit(0);
					break;

			}
			// terminating the program in case of inserting a duplicate ride
			if (terminate)
				break;
		}

		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Invalid Input\n");
			System.exit(0);
		}
		// processing the input
		
	}
}