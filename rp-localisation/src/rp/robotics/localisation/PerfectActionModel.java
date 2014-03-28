package rp.robotics.localisation;

import rp.robotics.mapping.Heading;

/**
 * Example structure for an action model that should move the probabilities 1
 * cell in the requested direction. In the case where the move would take the
 * robot into an obstacle or off the map, this model assumes the robot stayed in
 * one place. This is the same as the model presented in Robot Programming
 * Lecture 14.
 * 
 * Note that this class doesn't actually do this, instead it shows you a
 * <b>possible</b> structure for your action model.
 * 
 * @author nah
 * 
 */
public class PerfectActionModel implements ActionModel {

	@Override
	public GridPositionDistribution updateAfterMove(
			GridPositionDistribution _from, Heading _heading) {

		// Create the new distribution that will result from applying the action
		// model
		GridPositionDistribution to = new GridPositionDistribution(_from);

		// Move the probability in the correct direction for the action
		if (_heading == Heading.PLUS_X) {
			movePlusX(_from, to);
		} else if (_heading == Heading.PLUS_Y) {
			// you could implement a movePlusY etc. or you could find a way do
			// do all moves in a single method. Hint: all changes are just + or
			// - 1 to an x or y value.
			movePlusY(_from, to);
		} else if (_heading == Heading.MINUS_X) {
			moveMinusX(_from, to);

		} else if (_heading == Heading.MINUS_Y) {
			moveMinusY(_from, to);
		}

		return to;
	}

	/**
	 * Move probabilities from _from one cell in the plus x direction into _to
	 * 
	 * @param _from
	 * @param _to
	 */
	private void movePlusX(GridPositionDistribution _from,
			GridPositionDistribution _to) {

		moveHelper(_from, _to, Heading.PLUS_X);

	}

	private void moveMinusX(GridPositionDistribution _from,
			GridPositionDistribution _to) {

		moveHelper(_from, _to, Heading.MINUS_X);

	}

	private void movePlusY(GridPositionDistribution _from,
			GridPositionDistribution _to) {

		moveHelper(_from, _to, Heading.PLUS_Y);

	}

	private void moveMinusY(GridPositionDistribution _from,
			GridPositionDistribution _to) {

		moveHelper(_from, _to, Heading.MINUS_Y);

	}

	private void moveHelper(GridPositionDistribution _from,
			GridPositionDistribution _to, Heading _heading) {

		// _to.setProbability(0, 6, 0.6f);

		int i = 0;
		int j = 0;
		
		//Setting i and j values
		
		if (_heading == Heading.PLUS_X) {
			i = 1;
			j = 0;
		}
		
		if (_heading == Heading.MINUS_X) {
			i = -1;
			j = 0;
		}
		
		if (_heading == Heading.PLUS_Y) {
			i = 0;
			j = 1;
		}
		
		if (_heading == Heading.MINUS_Y) {
			i = 0;
			j = -1;
		}

		boolean intBack;
		boolean intFront;

		// iterate through points updating as appropriate
		for (int y = 0; y < _to.getGridHeight(); y++) {

			for (int x = 0; x < _to.getGridWidth(); x++) {

				// make sure to respect obstructed grid points
				if (!_to.isObstructed(x, y)) {

					// the action model should work out all of the different
					// ways (x,y) in the _to grid could've been reached based on
					// the _from grid and the move taken (in this case
					// HEADING.PLUS_X)

					// for example if the only way to have got to _to (x,y) was
					// from _from (x-1, y) (i.e. there was a PLUS_X move from
					// (x-1, y) then you write that to the (x, y) value

					// The below code does not move the value, just copies
					// it to the same position

					float fromProb;

					intBack = false;
					intFront = false;

					// checking surroundings

					// if right is obstructed
					if (!_from.getGridMap().isValidTransition(x, y, x + i, y + j)) {
						intFront = true;
						// System.out.println("Line between " + x +"," + y +
						// " and " + (x+1) + "," + y);
					}
					// if left is obstructed
					if (!_from.getGridMap().isValidTransition(x, y, x - i, y - j)) {
						intBack = true;
						// System.out.println("Line between " + x +"," + y +
						// " and " + (x+1) + "," + y);
					}
					// position after move

					// if x is trapped, prob stays the same
					if (intFront && intBack) {
						fromProb = _from.getProbability(x, y);
					}
					// if x is blocked by a right wall, prob = prob x + prob x
					// -1
					else if (intFront) {
						fromProb = _from.getProbability(x, y)
								+ _from.getProbability(x - i, y - j);
					}
					// if x is blocked by a left wall, prob = 0;
					else if (intBack) {
						fromProb = 0;
					}
					// if x is free, prob = prob x - 1
					// x is a free elf
					else // it is free and do below...
					{
						fromProb = _from.getProbability(x - i, y - i);
					}

					int toX = x;
					int toY = y;

					// set probability for position after move
					_to.setProbability(toX, toY, fromProb);

				}
			}
		}

	}

}
