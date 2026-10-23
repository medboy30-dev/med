package com.example.engine

import com.example.model.DifficultyLevel
import com.example.model.Puzzle
import com.example.model.PuzzleCategory
import kotlin.random.Random

object PuzzleValidator {
  fun validate(puzzle: Puzzle): Boolean {
    if (puzzle.question.isBlank()) return false
    if (puzzle.options.size < 2 || puzzle.options.size > 6) return false
    if (puzzle.options.distinct().size != puzzle.options.size) return false
    if (puzzle.correctIndex !in puzzle.options.indices) return false
    if (puzzle.explanation.isBlank()) return false
    return true
  }
}

object PuzzleGenerator {

  /**
   * Generates a 5-puzzle round containing 1 puzzle from each category:
   * 1. Logic
   * 2. Memory
   * 3. Observation
   * 4. Mathematics
   * 5. Pattern
   */
  fun generateRound(difficulty: DifficultyLevel = DifficultyLevel.NOVICE, seed: Long = System.currentTimeMillis()): List<Puzzle> {
    val random = Random(seed)
    val puzzles = mutableListOf<Puzzle>()

    puzzles.add(generateLogicPuzzle(random, difficulty))
    puzzles.add(generateMemoryPuzzle(random, difficulty))
    puzzles.add(generateObservationPuzzle(random, difficulty))
    puzzles.add(generateMathPuzzle(random, difficulty))
    puzzles.add(generatePatternPuzzle(random, difficulty))

    return puzzles.filter { PuzzleValidator.validate(it) }
  }

  /**
   * Generates deterministic daily puzzles for a given date string (e.g. "20261023").
   */
  fun generateDailyRound(dateKey: String): List<Puzzle> {
    val seed = dateKey.hashCode().toLong()
    return generateRound(difficulty = DifficultyLevel.ADEPT, seed = seed)
  }

  // --- LOGIC PUZZLES ---
  private fun generateLogicPuzzle(random: Random, diff: DifficultyLevel): Puzzle {
    val templates = listOf(
      {
        val circlesPerSquare = 2 + diff.ordinal
        val squares = 2
        val totalCircles = circlesPerSquare * squares
        val wrongs = listOf(circlesPerSquare, circlesPerSquare + 1, totalCircles + 2, totalCircles * 2)
          .filter { it != totalCircles && it > 0 }
          .distinct()
          .take(3)
        val allOptions = (wrongs + totalCircles).map { "$it" }.shuffled(random)
        Puzzle(
          id = "log_${random.nextInt()}",
          category = PuzzleCategory.LOGIC,
          title = "Scale Equilibrium",
          question = "1 Square balances $circlesPerSquare Circles. 1 Triangle balances $squares Squares. How many Circles balance 1 Triangle?",
          options = allOptions,
          correctIndex = allOptions.indexOf("$totalCircles"),
          explanation = "Since 1 Triangle = $squares Squares, and 1 Square = $circlesPerSquare Circles, 1 Triangle = $squares × $circlesPerSquare = $totalCircles Circles.",
          hint = "Substitute Squares with Circles in the Triangle equation.",
          difficulty = diff,
          timeLimitSeconds = 18
        )
      },
      {
        val names = listOf("Maya", "Leo", "Kai", "Zara").shuffled(random)
        val allOptions = names.shuffled(random)
        Puzzle(
          id = "log_${random.nextInt()}",
          category = PuzzleCategory.LOGIC,
          title = "Finish Line Deduction",
          question = "${names[0]} finished ahead of ${names[1]}. ${names[1]} finished ahead of ${names[2]}. ${names[3]} finished last. Who took 1st place?",
          options = allOptions,
          correctIndex = allOptions.indexOf(names[0]),
          explanation = "${names[0]} beat ${names[1]}, who beat ${names[2]}. With ${names[3]} last, ${names[0]} won the race.",
          hint = "Trace who is never behind anyone.",
          difficulty = diff,
          timeLimitSeconds = 15
        )
      },
      {
        val allOptions = listOf("West", "East", "South", "North-West").shuffled(random)
        Puzzle(
          id = "log_${random.nextInt()}",
          category = PuzzleCategory.LOGIC,
          title = "Compass Rotation",
          question = "You face North. You rotate 90° clockwise, then 180° counter-clockwise. Which direction do you face?",
          options = allOptions,
          correctIndex = allOptions.indexOf("West"),
          explanation = "North + 90° clockwise points East. East - 180° points directly opposite to West.",
          hint = "Calculate the net rotation: 90° clockwise minus 180° = 90° counter-clockwise from North.",
          difficulty = diff,
          timeLimitSeconds = 14
        )
      },
      {
        val allOptions = listOf("Both are true", "Box A only", "Box B only", "Neither is true").shuffled(random)
        Puzzle(
          id = "log_${random.nextInt()}",
          category = PuzzleCategory.LOGIC,
          title = "Truth & Statement",
          question = "Box A says: 'The gold is in Box B'. Box B says: 'Exactly one statement is true'. If the gold is in Box B, who is telling the truth?",
          options = allOptions,
          correctIndex = allOptions.indexOf("Both are true"),
          explanation = "If the gold is in Box B, Box A is telling the truth. Since Box A is true, Box B ('one is true') also matches reality.",
          hint = "Test whether Box A being true makes Box B's claim valid.",
          difficulty = diff,
          timeLimitSeconds = 18
        )
      }
    )
    return templates[random.nextInt(templates.size)]()
  }

  // --- MEMORY PUZZLES ---
  private fun generateMemoryPuzzle(random: Random, diff: DifficultyLevel): Puzzle {
    val symbols = listOf("⚡", "🧠", "💎", "🔥", "⭐", "🚀", "🛡️", "🔮")
    val count = 4 + (diff.ordinal.coerceAtMost(2))
    val sequence = symbols.shuffled(random).take(count)
    val targetIndex = random.nextInt(count)
    val targetSymbol = sequence[targetIndex]
    val positionWord = when (targetIndex) {
      0 -> "1st"
      1 -> "2nd"
      2 -> "3rd"
      3 -> "4th"
      4 -> "5th"
      else -> "${targetIndex + 1}th"
    }

    val wrongOptions = (symbols - targetSymbol).shuffled(random).take(3)
    val options = (wrongOptions + targetSymbol).shuffled(random)

    return Puzzle(
      id = "mem_${random.nextInt()}",
      category = PuzzleCategory.MEMORY,
      title = "Icon Sequence Recall",
      question = "Memorize the sequence:\n\n${sequence.joinToString("   ")}\n\nWhich symbol was in the $positionWord position?",
      options = options,
      correctIndex = options.indexOf(targetSymbol),
      explanation = "In the sequence [${sequence.joinToString(", ")}], the $positionWord icon is $targetSymbol.",
      hint = "Visualize the order from left to right.",
      difficulty = diff,
      timeLimitSeconds = 16,
      visualType = "SEQUENCE",
      visualData = sequence.joinToString(",")
    )
  }

  // --- OBSERVATION PUZZLES ---
  private fun generateObservationPuzzle(random: Random, diff: DifficultyLevel): Puzzle {
    // Stroop effect or Odd one out or counting
    val coin = random.nextInt(3)
    return when (coin) {
      0 -> {
        // Stroop Test
        val colors = listOf("RED" to 0xFFF43F5E, "BLUE" to 0xFF38BDF8, "GREEN" to 0xFF10B981, "YELLOW" to 0xFFFBBF24)
        val wordPair = colors[random.nextInt(colors.size)]
        val inkPair = (colors - wordPair).random(random)

        val wrongOptions = colors.map { it.first }.filter { it != inkPair.first }.shuffled(random).take(3)
        val allOptions = (wrongOptions + inkPair.first).shuffled(random)

        Puzzle(
          id = "obs_${random.nextInt()}",
          category = PuzzleCategory.OBSERVATION,
          title = "Stroop Color Interference",
          question = "Disregard the printed word! What INK COLOR is the text printed in?",
          options = allOptions,
          correctIndex = allOptions.indexOf(inkPair.first),
          explanation = "The word is '${wordPair.first}', but the visible INK COLOR is ${inkPair.first}.",
          hint = "Look at the visual pigment, not the letters.",
          difficulty = diff,
          timeLimitSeconds = 12,
          visualType = "STROOP",
          visualData = "${wordPair.first}:${inkPair.first}"
        )
      }
      1 -> {
        // Target Symbol Count
        val targets = listOf("★", "▲", "●", "◆")
        val chosenTarget = targets.random(random)
        val distractors = targets - chosenTarget
        val targetCount = random.nextInt(3, 7)
        val distractorCount = random.nextInt(8, 14)

        val stream = (List(targetCount) { chosenTarget } + List(distractorCount) { distractors.random(random) }).shuffled(random)
        val correctStr = "$targetCount"
        val wrongStrs = listOf("${targetCount - 1}", "${targetCount + 1}", "${targetCount + 2}").distinct()
        val options = (wrongStrs.take(3) + correctStr).shuffled(random)

        Puzzle(
          id = "obs_${random.nextInt()}",
          category = PuzzleCategory.OBSERVATION,
          title = "Rapid Target Scan",
          question = "How many '$chosenTarget' symbols appear below?\n\n${stream.joinToString(" ")}",
          options = options,
          correctIndex = options.indexOf(correctStr),
          explanation = "Counting carefully, '$chosenTarget' appears exactly $targetCount times.",
          hint = "Scan row by row from top-left.",
          difficulty = diff,
          timeLimitSeconds = 14
        )
      }
      else -> {
        // Odd one out
        val sets = listOf(
          Triple(listOf("Sphere", "Cube", "Cone", "Circle"), "Circle", "Circle is a 2D flat shape, while the others are 3D solids."),
          Triple(listOf("Mercury", "Venus", "Mars", "Moon"), "Moon", "The Moon is a natural satellite, while the others are planets."),
          Triple(listOf("Violin", "Guitar", "Cello", "Flute"), "Flute", "The Flute is a wind woodwind instrument, while the others are string instruments."),
          Triple(listOf("Oxygen", "Hydrogen", "Gold", "Nitrogen"), "Gold", "Gold is a solid metallic element at room temperature, while the others are gases.")
        )
        val chosen = sets.random(random)
        val options = chosen.first.shuffled(random)
        Puzzle(
          id = "obs_${random.nextInt()}",
          category = PuzzleCategory.OBSERVATION,
          title = "Odd Property Out",
          question = "Which item does NOT belong to the category?",
          options = options,
          correctIndex = options.indexOf(chosen.second),
          explanation = chosen.third,
          hint = "Look at dimensionality, state of matter, or classification.",
          difficulty = diff,
          timeLimitSeconds = 14
        )
      }
    }
  }

  // --- MATHEMATICS PUZZLES ---
  private fun generateMathPuzzle(random: Random, diff: DifficultyLevel): Puzzle {
    val kind = random.nextInt(3)
    return when (kind) {
      0 -> {
        // Mental arithmetic with operator precedence
        val a = random.nextInt(3, 9)
        val b = random.nextInt(3, 9)
        val c = random.nextInt(10, 40)
        val answer = (a * b) + c
        val wrongs = listOf(answer - a, answer + b, answer + 10, answer - 5, answer + 5)
          .filter { it != answer && it > 0 }
          .distinct()
          .take(3)
        val options = (wrongs.map { "$it" } + "$answer").shuffled(random)

        Puzzle(
          id = "mat_${random.nextInt()}",
          category = PuzzleCategory.MATH,
          title = "Order of Operations",
          question = "Calculate rapidly:\n\n$a × $b + $c = ?",
          options = options,
          correctIndex = options.indexOf("$answer"),
          explanation = "Multiply first: $a × $b = ${a * b}. Then add $c: ${a * b} + $c = $answer.",
          hint = "Multiplication takes precedence before addition.",
          difficulty = diff,
          timeLimitSeconds = 14
        )
      }
      1 -> {
        // Missing operator
        val x = random.nextInt(6, 15)
        val y = random.nextInt(2, 6)
        val ops = listOf(
          "+" to (x + y),
          "-" to (x - y),
          "×" to (x * y)
        )
        val (opSymbol, result) = ops.random(random)
        val allOps = listOf("+", "-", "×", "÷")

        Puzzle(
          id = "mat_${random.nextInt()}",
          category = PuzzleCategory.MATH,
          title = "Missing Operator",
          question = "Which operator makes the equation true?\n\n$x   [ ? ]   $y  =  $result",
          options = allOps,
          correctIndex = allOps.indexOf(opSymbol),
          explanation = "Replacing [ ? ] with '$opSymbol' yields: $x $opSymbol $y = $result.",
          hint = "Test each arithmetic sign in order.",
          difficulty = diff,
          timeLimitSeconds = 12
        )
      }
      else -> {
        // Sequence progression
        val step = random.nextInt(3, 8)
        val start = random.nextInt(2, 10)
        val seq = listOf(start, start + step, start + 2 * step, start + 3 * step)
        val nextVal = start + 4 * step
        val wrongs = listOf(nextVal - step, nextVal + 2, nextVal + step, nextVal + 4, nextVal - 1)
          .filter { it != nextVal && it > 0 }
          .distinct()
          .take(3)
        val options = (wrongs.map { "$it" } + "$nextVal").shuffled(random)

        Puzzle(
          id = "mat_${random.nextInt()}",
          category = PuzzleCategory.MATH,
          title = "Arithmetic Progression",
          question = "What is the next number in the series?\n\n${seq.joinToString(", ")},  [ ? ]",
          options = options,
          correctIndex = options.indexOf("$nextVal"),
          explanation = "The difference between each term is +$step. $seq[last] + $step = $nextVal.",
          hint = "Find the constant difference between consecutive terms.",
          difficulty = diff,
          timeLimitSeconds = 14
        )
      }
    }
  }

  // --- PATTERN PUZZLES ---
  private fun generatePatternPuzzle(random: Random, diff: DifficultyLevel): Puzzle {
    val patterns = listOf(
      {
        // Doubling sequence
        val factor = 2
        val start = random.nextInt(2, 6)
        val seq = listOf(start, start * factor, start * factor * factor, start * factor * factor * factor)
        val answer = seq.last() * factor
        val wrongs = listOf(answer - start, answer + factor, answer + factor * 2, answer - factor, answer + 4)
          .filter { it != answer && it > 0 }
          .distinct()
          .take(3)
        val options = (wrongs.map { "$it" } + "$answer").shuffled(random)
        Puzzle(
          id = "pat_${random.nextInt()}",
          category = PuzzleCategory.PATTERN,
          title = "Geometric Multiplication",
          question = "What comes next in the sequence?\n\n${seq.joinToString(", ")},  [ ? ]",
          options = options,
          correctIndex = options.indexOf("$answer"),
          explanation = "Each number is multiplied by $factor. ${seq.last()} × $factor = $answer.",
          hint = "Observe the ratio between successive terms.",
          difficulty = diff,
          timeLimitSeconds = 15
        )
      },
      {
        // Letter skipping: A, D, G, J, M (skip 2 letters)
        val alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        val skip = 3
        val startIdx = random.nextInt(0, 5)
        val letters = (0..3).map { alphabet[startIdx + it * skip] }
        val nextLetter = alphabet[startIdx + 4 * skip].toString()
        val wrongLetters = listOf(
          alphabet[startIdx + 4 * skip - 1].toString(),
          alphabet[(startIdx + 4 * skip + 1) % 26].toString(),
          alphabet[(startIdx + 4 * skip + 2) % 26].toString()
        ).distinct()
        val options = (wrongLetters.take(3) + nextLetter).shuffled(random)

        Puzzle(
          id = "pat_${random.nextInt()}",
          category = PuzzleCategory.PATTERN,
          title = "Alphabetical Skip",
          question = "Find the following letter:\n\n${letters.joinToString(", ")},  [ ? ]",
          options = options,
          correctIndex = options.indexOf(nextLetter),
          explanation = "Each letter advances by $skip positions in the alphabet. ${letters.last()} + $skip = $nextLetter.",
          hint = "Count how many letters are skipped between each item.",
          difficulty = diff,
          timeLimitSeconds = 14
        )
      },
      {
        // Analogy rule: Circle : Sphere :: Square : [ ? ]
        val analogies = listOf(
          Triple("Circle : Sphere :: Square : ?", "Cube", listOf("Triangle", "Pyramid", "Rectangle")),
          Triple("Hour : Clock :: Temperature : ?", "Thermometer", listOf("Barometer", "Speedometer", "Compass")),
          Triple("Author : Book :: Sculptor : ?", "Statue", listOf("Canvas", "Brush", "Palette")),
          Triple("Ocean : Water :: Desert : ?", "Sand", listOf("Oasis", "Cactus", "Dune"))
        )
        val picked = analogies.random(random)
        val options = (picked.third + picked.second).shuffled(random)

        Puzzle(
          id = "pat_${random.nextInt()}",
          category = PuzzleCategory.PATTERN,
          title = "Conceptual Analogy",
          question = "Complete the analogy:\n\n${picked.first}",
          options = options,
          correctIndex = options.indexOf(picked.second),
          explanation = "A Circle expands into a 3D Sphere, just as a 2D Square expands into a 3D Cube.",
          hint = "Identify the relationship between the first pair and apply it to the second.",
          difficulty = diff,
          timeLimitSeconds = 15
        )
      }
    )
    return patterns[random.nextInt(patterns.size)]()
  }
}
