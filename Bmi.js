const readline = require("readline/promises");

// PROCESS (plain functions, no classes)
function calculateBmi(weight, heightCm) {
    const heightM = heightCm / 100;
    return weight / (heightM * heightM);
}

function getCategory(bmi) {
    if (bmi < 18.5) return "Underweight";
    else if (bmi < 25) return "Normal";
    else if (bmi < 30) return "Overweight";
    else return "Obese";
}

// Runs top to bottom, step by step (procedural style)
async function main() {
    const rl = readline.createInterface({
        input: process.stdin,
        output: process.stdout,
    });

    console.log("===== BMI CALCULATOR =====");

    // INPUT
    const name = await rl.question("Name: ");
    const weight = Number(await rl.question("Weight (kg): "));
    const heightCm = Number(await rl.question("Height (cm): "));
    rl.close();

    // VALIDATION: stop if the numbers are not valid
    if (isNaN(weight) || isNaN(heightCm) || weight <= 0 || heightCm <= 0) {
        console.log("Error: enter a weight and height above 0.");  // same message as Java
        return;
    }

    // PROCESS
    const bmi = calculateBmi(weight, heightCm);
    const category = getCategory(bmi);

    // OUTPUT
    console.log();
    console.log("===== BMI RESULT =====");
    console.log("Name     : " + (name || "-"));
    console.log("Weight   : " + weight.toFixed(2) + " kg");
    console.log("Height   : " + heightCm.toFixed(2) + " cm");
    console.log("BMI      : " + bmi.toFixed(2));
    console.log("Category : " + category);
}

main();