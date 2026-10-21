const readline = require("readline/promises");

// ---------- PROCESS (plain functions, no classes) ----------
function parseBirthdate(text) {
    if (!/^\d{4}-\d{2}-\d{2}$/.test(text)) return null;
    const [y, m, d] = text.split("-").map(Number);
    const date = new Date(y, m - 1, d);
    const valid =
        date.getFullYear() === y && date.getMonth() === m - 1 && date.getDate() === d;
    if (!valid || date > new Date()) return null;
    return date;
}

function calculateAge(birthDate) {
    const today = new Date();
    let age = today.getFullYear() - birthDate.getFullYear();
    const hadBirthday =
        today.getMonth() > birthDate.getMonth() ||
        (today.getMonth() === birthDate.getMonth() && today.getDate() >= birthDate.getDate());
    if (!hadBirthday) age--;
    return age;
}

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

function getColor(category) {
    if (category === "Underweight") return "\x1b[44m"; // blue
    if (category === "Normal") return "\x1b[42m";      // green
    if (category === "Overweight") return "\x1b[43m";  // yellow
    return "\x1b[41m";                                 // red
}

// ---------- INPUT helpers (ask again until the answer is valid) ----------
async function askName(rl) {
    while (true) {
        const text = (await rl.question("Name: ")).trim();
        if (text !== "") return text;
        console.log("  Please enter a name.");
    }
}

async function askBirthdate(rl) {
    while (true) {
        const text = (await rl.question("Birthdate (YYYY-MM-DD): ")).trim();
        const date = parseBirthdate(text);
        if (date) return { text, date };
        console.log("  Use a real past date like 2000-05-31.");
    }
}

async function askSex(rl) {
    while (true) {
        const text = (await rl.question("Sex (M/F): ")).trim().toLowerCase();
        if (text === "m" || text === "male") return "Male";
        if (text === "f" || text === "female") return "Female";
        console.log("  Type M or F.");
    }
}

async function askPositive(rl, label) {
    while (true) {
        const value = Number(await rl.question(label));
        if (value > 0) return value;
        console.log("  Enter a number greater than 0.");
    }
}

// ---------- MAIN: runs top to bottom, like the Python version ----------
async function main() {
    const rl = readline.createInterface({
        input: process.stdin,
        output: process.stdout,
    });

    let again = true;
    while (again) {
        console.log("===== BMI CALCULATOR =====");

        // INPUT
        const name = await askName(rl);
        const birth = await askBirthdate(rl);
        const sex = await askSex(rl);
        const weight = await askPositive(rl, "Weight (kg): ");
        const heightCm = await askPositive(rl, "Height (cm): ");

        // PROCESS
        const age = calculateAge(birth.date);
        const bmi = calculateBmi(weight, heightCm);
        const category = getCategory(bmi);

        // OUTPUT
        console.log();
        console.log("===== BMI RESULT =====");
        console.log(getColor(category) + "\x1b[30m " + "BMI " + bmi.toFixed(2) + " - " + category + " \x1b[0m");
        console.log("Name     : " + name);
        console.log("Sex      : " + sex);
        console.log("Weight   : " + weight.toFixed(2) + " kg");
        console.log("Height   : " + heightCm.toFixed(2) + " cm");
        console.log("BMI      : " + bmi.toFixed(2));
        console.log("Category : " + category + " (Age: " + age + ", Birthdate: " + birth.text + ")");
        console.log();

        // CLEAR: start over with a clean screen
        const answer = (await rl.question("Calculate again? (y/n): ")).trim().toLowerCase();
        again = answer === "y" || answer === "yes";
        if (again) console.clear();
    }

    rl.close();
}

main();